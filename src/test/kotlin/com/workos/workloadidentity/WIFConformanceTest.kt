// @oagen-ignore-file
package com.workos.workloadidentity

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.http.Response
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import com.workos.WorkOS
import com.workos.common.exceptions.TransportException
import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.WorkOSException
import com.workos.common.exceptions.WorkloadIdentityException
import com.workos.common.http.Credentials
import com.workos.common.http.RequestConfig
import com.workos.common.http.RequestOptions
import com.workos.common.http.RetryConfig
import okhttp3.Call
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Dns
import okhttp3.EventListener
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import java.net.InetAddress
import java.net.Proxy
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * The workload identity conformance cases, WIF-01 to WIF-24. Each case ID is
 * in the name of at least one test here; the cases themselves are defined
 * once for all nine backend SDKs.
 *
 * Everything runs against local mocks: one WireMock server stands in for
 * both the token endpoint (`/oauth2/token`) and the WorkOS API (`/things`).
 * Time comes from an injected clock and backoff waits from an injected
 * sleeper, so no test sleeps to make time pass.
 */
class WIFConformanceTest {
  private val clock = AtomicLong(0)
  private val sleeps = CopyOnWriteArrayList<Long>()

  @AfterEach
  fun releaseHeldResponses() {
    hold.release()
  }

  // ---------------------------------------------------------------- fixtures

  private fun tokenUrl(): String = "${api.baseUrl()}$TOKEN_PATH"

  private fun credentials(
    provider: IdentityTokenProvider = IdentityTokenProvider { "assertion-1" },
    clientId: String? = CLIENT_ID,
    tokenUrl: String? = tokenUrl(),
    environmentId: String? = ENVIRONMENT_ID,
    resource: String? = null,
    scope: String? = null,
    httpClient: OkHttpClient? = null,
    env: (String) -> String? = { null },
    exchangeDeadlineMillis: Long = 30_000
  ): WorkloadIdentityCredentials =
    WorkloadIdentityCredentials(
      identityTokenProvider = provider,
      clientId = clientId,
      tokenUrl = tokenUrl,
      environmentId = environmentId,
      resource = resource,
      scope = scope,
      httpClient = httpClient,
      env = env,
      nanoTime = { clock.get() },
      sleeper = { sleeps += it },
      exchangeDeadlineMillis = exchangeDeadlineMillis
    )

  private fun client(
    credentials: Credentials,
    retryConfig: RetryConfig = RetryConfig.DISABLED
  ): WorkOS =
    WorkloadIdentity.client(
      credentials = credentials,
      apiBaseUrl = api.baseUrl(),
      httpClient = OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).build(),
      retryConfig = retryConfig
    )

  private fun getThings(
    workos: WorkOS,
    requestOptions: RequestOptions? = null
  ): Map<*, *> = workos.baseClient.request(RequestConfig("GET", API_PATH, requestOptions = requestOptions), Map::class.java)

  private fun advanceClockTo(seconds: Long) {
    clock.set(TimeUnit.SECONDS.toNanos(seconds))
  }

  // ------------------------------------------------------------------- stubs

  private fun tokenSuccess(
    accessToken: String,
    expiresIn: Int? = 300
  ): ResponseDefinitionBuilder {
    val expiry = if (expiresIn == null) "" else """, "expires_in": $expiresIn"""
    return aResponse()
      .withStatus(200)
      .withHeader("Content-Type", "application/json")
      .withBody("""{"access_token": "$accessToken", "token_type": "Bearer"$expiry}""")
  }

  private fun oauthError(
    code: String,
    description: String = "description for $code"
  ): ResponseDefinitionBuilder =
    aResponse()
      .withStatus(400)
      .withHeader("Content-Type", "application/json")
      .withBody("""{"error": "$code", "error_description": "$description"}""")

  private fun apiOk(): ResponseDefinitionBuilder =
    aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("""{"id": "t_1"}""")

  private fun apiStatus(status: Int): ResponseDefinitionBuilder =
    aResponse().withStatus(status).withHeader("Content-Type", "application/json").withBody("""{"message": "status $status"}""")

  /** Answer successive matching requests with [responses] in order; the last one repeats. */
  private fun stubSequence(
    name: String,
    mapping: () -> MappingBuilder,
    vararg responses: ResponseDefinitionBuilder
  ) {
    responses.forEachIndexed { index, response ->
      val state = if (index == 0) Scenario.STARTED else "$name-$index"
      val stub = mapping().inScenario(name).whenScenarioStateIs(state).willReturn(response)
      if (index < responses.lastIndex) stub.willSetStateTo("$name-${index + 1}")
      api.stubFor(stub)
    }
  }

  private fun stubTokenEndpoint(vararg responses: ResponseDefinitionBuilder) {
    stubSequence("token", { post(urlPathEqualTo(TOKEN_PATH)) }, *responses)
  }

  /** The token endpoint issues `access-1`, `access-2` and so on, one per exchange. */
  private fun stubTokens(expiresIn: Int? = 300) {
    stubTokenEndpoint(*(1..6).map { tokenSuccess("access-$it", expiresIn) }.toTypedArray())
  }

  private fun stubApi(vararg responses: ResponseDefinitionBuilder) {
    stubSequence("api", { any(urlPathEqualTo(API_PATH)) }, *responses)
  }

  // ------------------------------------------------------------ observations

  private fun exchanges(): List<LoggedRequest> = api.findAll(postRequestedFor(urlPathEqualTo(TOKEN_PATH)))

  private fun exchangeCount(): Int = exchanges().size

  private fun apiAttempts(): List<LoggedRequest> = api.findAll(anyRequestedFor(urlPathEqualTo(API_PATH)))

  private fun apiAttemptsWith(accessToken: String): Int =
    api
      .findAll(
        anyRequestedFor(urlPathEqualTo(API_PATH)).withHeader("Authorization", equalTo("Bearer $accessToken"))
      ).size

  private fun form(request: LoggedRequest): Map<String, String> =
    request.bodyAsString
      .split("&")
      .filter { it.isNotEmpty() }
      .associate { pair ->
        val (name, value) = pair.split("=", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
        URLDecoder.decode(name, Charsets.UTF_8) to URLDecoder.decode(value, Charsets.UTF_8)
      }

  private fun exchangeForms(): List<Map<String, String>> = exchanges().map { form(it) }

  /** Wait until every thread is parked, which here means waiting on the shared exchange. */
  private fun awaitParked(threads: List<Thread>) {
    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10)
    while (!threads.all { it.state == Thread.State.WAITING || it.state == Thread.State.TIMED_WAITING }) {
      check(System.nanoTime() < deadline) { "callers did not reach the shared exchange in time" }
      Thread.sleep(5)
    }
  }

  // ------------------------------------------------------- exchange and cache

  @Test
  fun `WIF-01 the first request sends one exchange POST with the right shape`() {
    stubTokens()
    stubApi(apiOk())

    getThings(client(credentials()))

    assertEquals(1, exchangeCount())
    val post = exchanges().single()
    assertEquals("application/x-www-form-urlencoded", post.getHeader("Content-Type"))
    assertFalse(post.containsHeader("Authorization"))
    val form = form(post)
    assertEquals("urn:ietf:params:oauth:grant-type:jwt-bearer", form["grant_type"])
    assertEquals("assertion-1", form["assertion"])
    assertEquals(CLIENT_ID, form["client_id"])
    assertEquals("https://api.workos.com", form["resource"])
    assertFalse(form.containsKey("scope"))
    assertFalse(form.containsKey("client_secret"))

    // A new instance with a scope sends it.
    api.resetRequests()
    getThings(client(credentials(scope = "orgs:write")))

    assertEquals(1, exchangeCount())
    val scoped = form(exchanges().single())
    assertEquals("orgs:write", scoped["scope"])
    assertFalse(scoped.containsKey("client_secret"))
    assertFalse(exchanges().single().containsHeader("Authorization"))
  }

  @Test
  fun `WIF-02 the API request carries the access token and the environment header`() {
    stubTokens()
    stubApi(apiOk())

    getThings(client(credentials()))

    val attempt = apiAttempts().single()
    assertEquals("Bearer access-1", attempt.getHeader("Authorization"))
    assertEquals(ENVIRONMENT_ID, attempt.getHeader("x-url-environment-id"))
  }

  @Test
  fun `WIF-02 without an environment ID the API request carries the access token and no environment header`() {
    stubTokens()
    stubApi(apiOk())

    // No environment ID is passed and WORKOS_ENVIRONMENT_ID is unset. Constructing the credentials is not an error.
    val unset = credentials(environmentId = null, env = { null })
    assertNull(unset.environmentId)
    getThings(client(unset))

    val attempt = apiAttempts().single()
    assertEquals("Bearer access-1", attempt.getHeader("Authorization"))
    assertFalse(attempt.containsHeader("x-url-environment-id"))

    // An empty string counts as not set, whether it is passed or comes from the environment.
    api.resetRequests()
    val empty = credentials(environmentId = "", env = { name -> if (name == "WORKOS_ENVIRONMENT_ID") "" else null })
    assertNull(empty.environmentId)
    getThings(client(empty))

    assertFalse(apiAttempts().single().containsHeader("x-url-environment-id"))
  }

  @Test
  fun `WIF-03 a second request within the token lifetime reuses the cached token`() {
    stubTokens()
    stubApi(apiOk())
    val workos = client(credentials())

    getThings(workos)
    getThings(workos)

    assertEquals(1, exchangeCount())
    assertEquals(2, apiAttemptsWith("access-1"))
  }

  @Test
  fun `WIF-04 a 300 second token is refreshed when less than 60 seconds remain`() {
    stubTokens(expiresIn = 300)
    stubApi(apiOk())
    val workos = client(credentials())

    getThings(workos)
    advanceClockTo(239)
    getThings(workos)
    assertEquals(1, exchangeCount())

    advanceClockTo(241)
    getThings(workos)
    assertEquals(2, exchangeCount())
    assertEquals(2, apiAttemptsWith("access-1"))
    assertEquals(1, apiAttemptsWith("access-2"))
  }

  @Test
  fun `WIF-05 a 100 second token is refreshed when less than 20 seconds remain`() {
    stubTokens(expiresIn = 100)
    stubApi(apiOk())
    val workos = client(credentials())

    getThings(workos)
    advanceClockTo(79)
    getThings(workos)
    assertEquals(1, exchangeCount())

    advanceClockTo(81)
    getThings(workos)
    assertEquals(2, exchangeCount())
    assertEquals(1, apiAttemptsWith("access-2"))
  }

  @Test
  fun `WIF-06 a response with no expires_in is treated as 300 seconds`() {
    stubTokens(expiresIn = null)
    stubApi(apiOk())
    val workos = client(credentials())

    getThings(workos)
    advanceClockTo(239)
    getThings(workos)
    assertEquals(1, exchangeCount())

    advanceClockTo(241)
    getThings(workos)
    assertEquals(2, exchangeCount())
    assertEquals(1, apiAttemptsWith("access-2"))
  }

  @Test
  fun `WIF-07 two credentials instances never share a cached token`() {
    stubTokens()
    stubApi(apiOk())
    val first = client(credentials())
    val second = client(credentials())

    getThings(first)
    assertEquals(1, apiAttemptsWith("access-1"))
    getThings(second)

    assertEquals(2, exchangeCount())
    assertEquals(1, apiAttemptsWith("access-1"))
    assertEquals(1, apiAttemptsWith("access-2"))
  }

  @Test
  fun `WIF-08 twenty concurrent first requests on one instance produce exactly one exchange`() {
    hold.arm()
    stubTokenEndpoint(tokenSuccess("access-1").withTransformers(HoldUntilReleased.NAME), tokenSuccess("access-2"))
    stubApi(apiOk())
    val providerCalls = AtomicInteger()
    val workos =
      client(
        credentials(
          provider = {
            providerCalls.incrementAndGet()
            "assertion-1"
          }
        )
      )
    val failures = CopyOnWriteArrayList<Throwable>()
    val callers =
      (1..20).map {
        Thread {
          try {
            getThings(workos)
          } catch (e: Throwable) {
            failures += e
          }
        }
      }

    callers.forEach { it.start() }
    // The token endpoint holds its response until all twenty are waiting.
    assertTrue(hold.awaitEntered(), "the exchange never reached the token endpoint")
    awaitParked(callers)
    hold.release()
    callers.forEach { it.join(TimeUnit.SECONDS.toMillis(10)) }

    assertEquals(emptyList<Throwable>(), failures.toList())
    assertEquals(1, exchangeCount())
    assertEquals(1, providerCalls.get())
    assertEquals(20, apiAttemptsWith("access-1"))
    assertEquals(20, apiAttempts().size)
  }

  // ------------------------------------------------------------ 401 recovery

  @Test
  fun `WIF-09 a 401 invalidates that token generation and exchanges again and retries once`() {
    stubTokens()
    // The stubs pin the order: only access-1 can get the 401, only access-2 the 200.
    api.stubFor(
      get(urlPathEqualTo(API_PATH)).withHeader("Authorization", equalTo("Bearer access-1")).willReturn(apiStatus(401))
    )
    api.stubFor(
      get(urlPathEqualTo(API_PATH)).withHeader("Authorization", equalTo("Bearer access-2")).willReturn(apiOk())
    )

    val result = getThings(client(credentials()))

    assertEquals("t_1", result["id"])
    assertEquals(2, exchangeCount())
    assertEquals(2, apiAttempts().size)
    assertEquals(1, apiAttemptsWith("access-1"))
    assertEquals(1, apiAttemptsWith("access-2"))
  }

  @Test
  fun `WIF-10 a second 401 after recovery is returned to the caller`() {
    stubTokens()
    stubApi(apiStatus(401))

    assertThrows(UnauthorizedException::class.java) { getThings(client(credentials())) }

    assertEquals(2, exchangeCount())
    assertEquals(2, apiAttempts().size)
  }

  @Test
  fun `WIF-11 a late 401 for an old generation does not discard a newer token`() {
    stubTokens()
    stubApi(apiOk())
    val credentials = credentials()

    val first = credentials.resolve(null)
    assertEquals("Bearer access-1", first.headers["Authorization"])
    credentials.invalidate(first.generation)
    val second = credentials.resolve(null)
    assertEquals("Bearer access-2", second.headers["Authorization"])
    assertNotEquals(first.generation, second.generation)

    // A late 401 for the first token arrives after the second is cached.
    credentials.invalidate(first.generation)
    getThings(client(credentials))

    assertEquals(second.generation, credentials.resolve(null).generation)
    assertEquals(2, exchangeCount())
    assertEquals("Bearer access-2", apiAttempts().single().getHeader("Authorization"))
  }

  @Test
  fun `WIF-12 transport retries inside one logical request do not reset the single 401 recovery`() {
    stubTokens()
    // The 503 carries no Retry-After on purpose: OkHttp itself resends a 503 that says `Retry-After: 0`,
    // below the interceptor, and the SDK's retry loop would never run. A zero backoff keeps the test fast.
    stubApi(apiStatus(401), apiStatus(503), apiStatus(401), apiOk())
    val immediateRetries = RetryConfig(baseDelayMs = 0, maxDelayMs = 0, jitter = 0.0)

    assertThrows(UnauthorizedException::class.java) {
      getThings(client(credentials(), retryConfig = immediateRetries))
    }

    assertEquals(2, exchangeCount())
    assertEquals(3, apiAttempts().size)
  }

  @Test
  fun `WIF-13 the 401 retry sends the same request body and the same idempotency key`() {
    stubTokens()
    stubApi(apiStatus(401), apiOk())
    // Retries must be on for the SDK to generate an idempotency key for a POST that has none.
    val workos = client(credentials(), retryConfig = RetryConfig.DEFAULT)

    workos.baseClient.request(
      RequestConfig("POST", API_PATH, body = mapOf("name" to "thing", "tags" to listOf("a", "b"))),
      Map::class.java
    )

    val attempts = api.findAll(postRequestedFor(urlPathEqualTo(API_PATH)))
    assertEquals(2, attempts.size)
    assertTrue(attempts[0].body.isNotEmpty())
    assertArrayEquals(attempts[0].body, attempts[1].body)
    val key = attempts[0].getHeader("Idempotency-Key")
    assertFalse(key.isNullOrEmpty())
    assertEquals(key, attempts[1].getHeader("Idempotency-Key"))
    assertEquals(2, exchangeCount())
  }

  // --------------------------------------------------------- exchange retries

  @Test
  fun `WIF-14 the exchange is retried on 503 and 429 and calls the provider again each attempt`() {
    stubTokenEndpoint(
      aResponse().withStatus(503),
      aResponse().withStatus(429).withHeader("Retry-After", "0"),
      tokenSuccess("access-1")
    )
    stubApi(apiOk())
    val providerCalls = AtomicInteger()

    getThings(client(credentials(provider = { "assertion-${providerCalls.incrementAndGet()}" })))

    assertEquals(3, exchangeCount())
    assertEquals(3, providerCalls.get())
    assertEquals(1, apiAttemptsWith("access-1"))
    // The body is rebuilt for every attempt.
    assertEquals(setOf("assertion-1", "assertion-2", "assertion-3"), exchangeForms().map { it["assertion"] }.toSet())
  }

  @Test
  fun `WIF-14 the exchange is retried after a connection failure`() {
    stubTokenEndpoint(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER), tokenSuccess("access-1"))
    stubApi(apiOk())
    val providerCalls = AtomicInteger()

    getThings(client(credentials(provider = { "assertion-${providerCalls.incrementAndGet()}" })))

    assertEquals(2, providerCalls.get())
    assertEquals(1, apiAttemptsWith("access-1"))
  }

  @Test
  fun `WIF-14 a provider failure is retried by calling the provider again`() {
    stubTokens()
    stubApi(apiOk())
    val providerCalls = AtomicInteger()
    val provider =
      IdentityTokenProvider {
        if (providerCalls.incrementAndGet() == 1) error("the platform was not ready")
        "assertion-2"
      }

    getThings(client(credentials(provider = provider)))

    assertEquals(2, providerCalls.get())
    assertEquals(1, exchangeCount())
    assertEquals("assertion-2", exchangeForms().single()["assertion"])
    assertEquals(1, apiAttemptsWith("access-1"))
  }

  @Test
  fun `WIF-14 the exchange honors Retry-After before the next attempt`() {
    stubTokenEndpoint(aResponse().withStatus(429).withHeader("Retry-After", "1"), tokenSuccess("access-1"))
    stubApi(apiOk())

    getThings(client(credentials()))

    assertEquals(2, exchangeCount())
    assertEquals(1, sleeps.size)
    assertTrue(sleeps.single() >= 1000, "expected a wait of at least one second, got ${sleeps.single()} ms")
  }

  // ------------------------------------------------------------------ errors

  @Test
  fun `WIF-15 an OAuth error response is not retried and surfaces as a typed error with the code`() {
    val codes =
      listOf("invalid_grant", "invalid_target", "invalid_client", "unauthorized_client", "invalid_scope", "invalid_request")
    val hints = mutableSetOf<String>()
    for (code in codes) {
      api.resetAll()
      stubTokenEndpoint(oauthError(code, "the server says $code"))
      stubApi(apiOk())

      val error = assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials())) }

      assertEquals(code, error.code)
      assertEquals("the server says $code", error.description)
      assertTrue(error.hint.isNotEmpty())
      assertEquals("$code: ${error.hint} the server says $code", error.message)
      assertEquals(1, exchangeCount(), "exchange count for $code")
      assertEquals(0, apiAttempts().size)
      hints += error.hint
    }
    // Each of the six codes has its own hint.
    assertEquals(codes.size, hints.size)
  }

  @Test
  fun `WIF-16 after a failed exchange the next request attempts a fresh exchange`() {
    stubTokenEndpoint(oauthError("invalid_grant"), tokenSuccess("access-1"))
    stubApi(apiOk())
    val workos = client(credentials())

    assertThrows(WorkloadIdentityException::class.java) { getThings(workos) }
    getThings(workos)

    assertEquals(2, exchangeCount())
    assertEquals(1, apiAttemptsWith("access-1"))
  }

  @Test
  fun `WIF-17 neither the assertion nor the access token appears in any error or output`() {
    val assertion = "SECRET-ASSERTION-VALUE"
    val accessToken = "SECRET-ACCESS-VALUE"
    val errors = mutableListOf<Throwable>()
    val originalOut = System.out
    val originalErr = System.err
    val captured = ByteArrayOutputStream()
    System.setOut(PrintStream(captured, true))
    System.setErr(PrintStream(captured, true))
    try {
      // An exchange the token endpoint refuses.
      stubTokenEndpoint(oauthError("invalid_grant"))
      stubApi(apiOk())
      errors += assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials(provider = { assertion }))) }

      // A token endpoint that fails until the retries run out.
      api.resetAll()
      stubTokenEndpoint(aResponse().withStatus(500).withBody("""{"echo": "$assertion"}"""))
      stubApi(apiOk())
      errors += assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials(provider = { assertion }))) }
      assertEquals(RetryConfig.DEFAULT.maxRetries + 1, exchangeCount())

      // A provider that raises.
      api.resetAll()
      stubTokens()
      stubApi(apiOk())
      val failing = IdentityTokenProvider { error("the platform refused to issue a token") }
      errors += assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials(provider = failing))) }
      assertEquals(0, exchangeCount())

      // A second 401 from the API.
      api.resetAll()
      stubTokenEndpoint(tokenSuccess(accessToken))
      stubApi(apiStatus(401))
      errors += assertThrows(UnauthorizedException::class.java) { getThings(client(credentials(provider = { assertion }))) }
      assertEquals(2, apiAttemptsWith(accessToken))
    } finally {
      System.setOut(originalOut)
      System.setErr(originalErr)
    }

    assertEquals(4, errors.size)
    val texts = errors.flatMap { representations(it) } + captured.toString(Charsets.UTF_8)
    for (text in texts) {
      assertFalse(text.contains(assertion), "the assertion leaked into: $text")
      assertFalse(text.contains(accessToken), "the access token leaked into: $text")
    }
  }

  /** Every way an error is likely to be printed: message, string form, fields and the full stack trace with causes. */
  private fun representations(error: Throwable): List<String> {
    val trace = StringWriter().also { error.printStackTrace(PrintWriter(it)) }.toString()
    val fields =
      when (error) {
        is WorkloadIdentityException -> listOf(error.code.orEmpty(), error.description, error.hint, error.rawBody.orEmpty())
        is WorkOSException -> listOf(error.code.orEmpty(), error.rawBody.orEmpty())
        else -> emptyList()
      }
    return listOf(error.message.orEmpty(), error.toString(), trace) + fields
  }

  @Test
  fun `WIF-18 the exchange request does not follow redirects`() {
    stubTokenEndpoint(
      aResponse().withStatus(307).withHeader("Location", "${elsewhere.baseUrl()}$TOKEN_PATH")
    )
    stubApi(apiOk())
    elsewhere.stubFor(any(anyUrl()).willReturn(tokenSuccess("access-from-elsewhere")))

    val error = assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials())) }

    assertEquals(WorkloadIdentityException.EXCHANGE_FAILED, error.code)
    assertEquals(0, elsewhere.findAll(anyRequestedFor(anyUrl())).size)
    assertEquals(1, exchangeCount())
    assertEquals(0, apiAttempts().size)
  }

  // -------------------------------------------------------------- precedence

  @Test
  fun `WIF-19 the credentials factory accepts no API key and its client never sends a static key`() {
    // The factory has no parameter a key could be passed through, so a key and credentials cannot conflict.
    val parameters = WorkloadIdentity::client.parameters.mapNotNull { it.name }
    assertEquals(listOf("credentials", "clientId", "apiBaseUrl", "httpClient", "retryConfig"), parameters)
    assertEquals(ENV_API_KEY, System.getenv("WORKOS_API_KEY"), "the test JVM must run with WORKOS_API_KEY set")
    stubTokens()
    stubApi(apiOk())

    val workos = client(credentials())
    getThings(workos)

    assertEquals("", workos.apiKey)
    assertEquals("Bearer access-1", apiAttempts().single().getHeader("Authorization"))
    assertNeverSent(ENV_API_KEY)
  }

  @Test
  fun `WIF-20 explicit credentials win over WORKOS_API_KEY and a failing configuration never falls back to it`() {
    assertEquals(ENV_API_KEY, System.getenv("WORKOS_API_KEY"), "the test JVM must run with WORKOS_API_KEY set")
    val environment: (String) -> String? = { name -> if (name == "WORKOS_API_KEY") ENV_API_KEY else null }

    // (a) Valid credentials: the API sees the access token, never the environment key.
    stubTokens()
    stubApi(apiOk())
    getThings(client(credentials(env = environment)))
    assertEquals("Bearer access-1", apiAttempts().single().getHeader("Authorization"))
    assertNeverSent(ENV_API_KEY)

    // (b) No token URL, explicitly or in the environment: constructing the credentials is an error.
    val missing =
      assertThrows(WorkloadIdentityException::class.java) { credentials(tokenUrl = null, env = environment) }
    assertEquals(WorkloadIdentityException.INVALID_CONFIGURATION, missing.code)
    assertEquals("tokenUrl is required (or set WORKOS_WIF_TOKEN_URL)", missing.description)

    // (c) An exchange that fails: the request fails, and nothing falls back to the environment key.
    api.resetAll()
    stubTokenEndpoint(oauthError("invalid_client"))
    stubApi(apiOk())
    val refused =
      assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials(env = environment))) }
    assertEquals("invalid_client", refused.code)
    assertEquals(0, apiAttempts().size)
    assertNeverSent(ENV_API_KEY)
  }

  private fun assertNeverSent(secret: String) {
    val leaked = api.findAll(anyRequestedFor(anyUrl()).withHeader("Authorization", containing(secret)))
    assertEquals(0, leaked.size, "a request carried $secret")
    assertTrue(api.findAll(anyRequestedFor(anyUrl())).none { it.bodyAsString.contains(secret) })
  }

  // --------------------------------------------------------------- providers

  @Test
  fun `WIF-21 tokenFile re-reads the file on every exchange`(
    @TempDir directory: Path
  ) {
    val file = directory.resolve("identity-token")
    Files.writeString(file, "assertion-1\n")
    // The stubs pin the order: each assertion is answered with its own access token.
    api.stubFor(
      post(urlPathEqualTo(TOKEN_PATH)).withRequestBody(containing("assertion=assertion-1")).willReturn(tokenSuccess("access-1"))
    )
    api.stubFor(
      post(urlPathEqualTo(TOKEN_PATH)).withRequestBody(containing("assertion=assertion-2")).willReturn(tokenSuccess("access-2"))
    )
    stubApi(apiOk())
    val workos = client(credentials(provider = tokenFile(file.toString())))

    getThings(workos)
    assertEquals(1, apiAttemptsWith("access-1"))

    Files.writeString(file, "  assertion-2\n\n")
    advanceClockTo(300)
    getThings(workos)

    assertEquals(listOf("assertion-1", "assertion-2"), exchangeForms().map { it.getValue("assertion") }.sorted())
    assertEquals(1, apiAttemptsWith("access-2"))
  }

  @Test
  fun `WIF-21 tokenFile fails for a missing or empty file`(
    @TempDir directory: Path
  ) {
    val empty = directory.resolve("empty")
    Files.writeString(empty, " \n")

    assertThrows(IllegalStateException::class.java) { tokenFile(directory.resolve("missing").toString()).getToken() }
    assertThrows(IllegalStateException::class.java) { tokenFile(empty.toString()).getToken() }
  }

  @Test
  fun `WIF-22 tokenEnv and callback providers supply the assertion`() {
    stubTokens()
    stubApi(apiOk())

    // (a) tokenEnv reads the variable on every call.
    val variables = mutableMapOf("MY_JWT" to "assertion-env")
    val fromEnvironment = tokenEnv("MY_JWT") { variables[it] }
    getThings(client(credentials(provider = fromEnvironment)))
    assertEquals("assertion-env", exchangeForms().single()["assertion"])
    variables.remove("MY_JWT")
    assertThrows(IllegalStateException::class.java) { fromEnvironment.getToken() }
    assertThrows(IllegalStateException::class.java) { tokenEnv("WORKOS_WIF_TEST_UNSET_VARIABLE").getToken() }

    // (b) A callback is any lambda of the provider type.
    api.resetRequests()
    getThings(client(credentials(provider = { "assertion-cb" })))
    assertEquals("assertion-cb", exchangeForms().single()["assertion"])
  }

  @Test
  fun `WIF-22 omitted fields default from the environment and explicit values win`() {
    stubTokens()
    stubApi(apiOk())
    val environment =
      mapOf(
        "WORKOS_CLIENT_ID" to "client_env",
        "WORKOS_WIF_TOKEN_URL" to tokenUrl(),
        "WORKOS_WIF_SCOPE" to "scope_env",
        "WORKOS_ENVIRONMENT_ID" to "environment_env"
      )

    // (c) Only a provider is passed; everything else comes from the environment.
    val defaulted =
      credentials(clientId = null, tokenUrl = null, environmentId = null, env = { environment[it] })
    getThings(client(defaulted))
    val defaultedForm = exchangeForms().single()
    assertEquals("client_env", defaultedForm["client_id"])
    assertEquals("scope_env", defaultedForm["scope"])
    assertEquals("environment_env", apiAttempts().single().getHeader("x-url-environment-id"))

    // (d) Same environment, explicit values: the explicit values are sent.
    api.resetRequests()
    val explicit =
      credentials(
        clientId = "client_explicit",
        tokenUrl = null,
        environmentId = "environment_explicit",
        scope = "scope_explicit",
        env = { environment[it] }
      )
    getThings(client(explicit))
    val explicitForm = exchangeForms().single()
    assertEquals("client_explicit", explicitForm["client_id"])
    assertEquals("scope_explicit", explicitForm["scope"])
    assertEquals("environment_explicit", apiAttempts().single().getHeader("x-url-environment-id"))
  }

  // ------------------------------------------------------------- cancellation

  @Test
  fun `WIF-23 a caller that times out does not cancel the shared exchange for other callers`() {
    hold.arm()
    stubTokenEndpoint(tokenSuccess("access-1").withTransformers(HoldUntilReleased.NAME), tokenSuccess("access-2"))
    stubApi(apiOk())
    val workos = client(credentials())
    val outcomeA = AtomicReference<Throwable>()
    val outcomeB = AtomicReference<Any>()
    val callerA =
      Thread {
        try {
          getThings(workos, RequestOptions(timeoutMillis = 300))
        } catch (e: Throwable) {
          outcomeA.set(e)
        }
      }
    val callerB =
      Thread {
        try {
          outcomeB.set(getThings(workos))
        } catch (e: Throwable) {
          outcomeB.set(e)
        }
      }

    callerA.start()
    callerB.start()
    assertTrue(hold.awaitEntered(), "the exchange never reached the token endpoint")
    // A gives up while the token endpoint is still holding its response.
    callerA.join(TimeUnit.SECONDS.toMillis(10))
    assertFalse(callerA.isAlive)
    awaitParked(listOf(callerB))
    hold.release()
    callerB.join(TimeUnit.SECONDS.toMillis(10))

    val failure = assertInstanceOf(TransportException::class.java, outcomeA.get())
    assertTrue(failure.message.orEmpty().contains("timeout", ignoreCase = true), "A should fail with a timeout: ${failure.message}")
    // OkHttp's own call timeout can fire first and wrap ours, so look down the cause chain for what timed out.
    assertTrue(
      generateSequence<Throwable>(failure) { it.cause }.any { it.message.orEmpty().contains("workload identity credentials") },
      "the failure should say it was waiting for credentials"
    )
    assertEquals("t_1", assertInstanceOf(Map::class.java, outcomeB.get())["id"])
    assertEquals(1, exchangeCount())
    assertEquals(1, apiAttemptsWith("access-1"))
    assertEquals(1, apiAttempts().size)
  }

  @Test
  fun `WIF-23 an interrupted caller does not cancel the shared exchange for other callers`() {
    hold.arm()
    stubTokenEndpoint(tokenSuccess("access-1").withTransformers(HoldUntilReleased.NAME), tokenSuccess("access-2"))
    val credentials = credentials()
    val outcomeA = AtomicReference<Any>()
    val outcomeB = AtomicReference<Any>()
    val callerA =
      Thread {
        try {
          outcomeA.set(credentials.resolve(null))
        } catch (e: Throwable) {
          outcomeA.set(e)
        }
      }
    val callerB =
      Thread {
        try {
          outcomeB.set(credentials.resolve(null).headers["Authorization"])
        } catch (e: Throwable) {
          outcomeB.set(e)
        }
      }

    callerA.start()
    callerB.start()
    assertTrue(hold.awaitEntered(), "the exchange never reached the token endpoint")
    awaitParked(listOf(callerA, callerB))
    callerA.interrupt()
    callerA.join(TimeUnit.SECONDS.toMillis(10))
    assertInstanceOf(InterruptedException::class.java, outcomeA.get())

    hold.release()
    callerB.join(TimeUnit.SECONDS.toMillis(10))

    assertEquals("Bearer access-1", outcomeB.get())
    assertEquals(1, exchangeCount())
  }

  // --------------------------------------------------------------- static key

  @Test
  fun `WIF-24 static API-key clients are unchanged`() {
    stubTokens()
    api.stubFor(get(urlPathEqualTo(API_PATH)).willReturn(apiOk()))
    api.stubFor(get(urlPathEqualTo("/denied")).willReturn(apiStatus(401)))
    val workos =
      WorkOS(
        apiKey = "sk_test_static",
        apiBaseUrl = api.baseUrl(),
        httpClient = OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).build(),
        retryConfig = RetryConfig.DEFAULT
      )

    getThings(workos)

    val attempt = apiAttempts().single()
    assertEquals("Bearer sk_test_static", attempt.getHeader("Authorization"))
    assertFalse(attempt.containsHeader("x-url-environment-id"))

    assertThrows(UnauthorizedException::class.java) {
      workos.baseClient.request(RequestConfig("GET", "/denied"), Map::class.java)
    }

    val denied = api.findAll(getRequestedFor(urlPathEqualTo("/denied")))
    assertEquals(1, denied.size)
    assertEquals("Bearer sk_test_static", denied.single().getHeader("Authorization"))
    assertEquals(0, exchangeCount())
  }

  // ------------------------------------------- seam details beyond the cases

  @Test
  fun `a per-request API key keeps its precedence on a credentials client and is not recovered`() {
    stubTokens()
    stubApi(apiStatus(401))
    val workos = client(credentials())

    assertThrows(UnauthorizedException::class.java) {
      getThings(workos, RequestOptions(apiKey = "sk_test_override"))
    }

    val attempt = apiAttempts().single()
    assertEquals("Bearer sk_test_override", attempt.getHeader("Authorization"))
    assertFalse(attempt.containsHeader("x-url-environment-id"))
    assertEquals(0, exchangeCount())
  }

  @Test
  fun `the credential headers replace an Authorization header passed as an additional header`() {
    stubTokens()
    stubApi(apiOk())

    getThings(client(credentials()), RequestOptions(additionalHeaders = mapOf("Authorization" to "Bearer from-a-header")))

    assertEquals("Bearer access-1", apiAttempts().single().getHeader("Authorization"))
  }

  @Test
  fun `the Java builder produces the same credentials as the constructor`() {
    stubTokens()
    stubApi(apiOk())
    val credentials =
      WorkloadIdentityCredentials
        .builder()
        .identityTokenProvider { "assertion-builder" }
        .clientId(CLIENT_ID)
        .tokenUrl(tokenUrl())
        .environmentId(ENVIRONMENT_ID)
        .resource("https://resource.example.com")
        .scope("organizations:read")
        .build()

    getThings(client(credentials))

    val form = exchangeForms().single()
    assertEquals("assertion-builder", form["assertion"])
    assertEquals("https://resource.example.com", form["resource"])
    assertEquals("organizations:read", form["scope"])
    val unset = assertThrows(WorkloadIdentityException::class.java) { WorkloadIdentityCredentials.builder().build() }
    assertEquals(WorkloadIdentityException.INVALID_CONFIGURATION, unset.code)
  }

  // ------------------------------------------------------ httpClient setting

  @Test
  fun `the exchange uses the connection settings of the httpClient setting`() {
    stubTokens()
    stubApi(apiOk())
    // Only this client's DNS can resolve the token endpoint's host name, so
    // the exchange succeeds only if it is made with this client's settings.
    val host = "token-endpoint.invalid"
    val lookups = CopyOnWriteArrayList<String>()
    val dns =
      object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
          lookups += hostname
          return if (hostname == host) listOf(InetAddress.getLoopbackAddress()) else Dns.SYSTEM.lookup(hostname)
        }
      }
    val credentials =
      WorkloadIdentityCredentials
        .builder()
        .identityTokenProvider { "assertion-1" }
        .clientId(CLIENT_ID)
        .tokenUrl("http://$host:${api.port}$TOKEN_PATH")
        .environmentId(ENVIRONMENT_ID)
        .httpClient(
          OkHttpClient
            .Builder()
            .dns(dns)
            .proxy(Proxy.NO_PROXY)
            .build()
        ).build()

    getThings(client(credentials))

    assertEquals(listOf(host), lookups.toList())
    assertEquals("assertion-1", exchangeForms().single()["assertion"])
    assertEquals(1, apiAttemptsWith("access-1"))
  }

  @Test
  fun `the exchange does not run the interceptors or event listener or authenticator or cookie jar of the httpClient setting`() {
    stubTokenEndpoint(
      aResponse().withStatus(401).withHeader("Content-Type", "application/json").withBody("""{"error": "invalid_client"}""")
    )
    stubApi(apiOk())
    val ran = CopyOnWriteArrayList<String>()
    val eventListener =
      object : EventListener() {
        override fun callStart(call: Call) {
          ran += "event listener"
        }
      }
    val cookieJar =
      object : CookieJar {
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
          ran += "cookie jar"
          return listOf(
            Cookie
              .Builder()
              .name("session")
              .value("from-cookie-jar")
              .domain(url.host)
              .build()
          )
        }

        override fun saveFromResponse(
          url: HttpUrl,
          cookies: List<Cookie>
        ) {
          ran += "cookie jar"
        }
      }
    val httpClient =
      OkHttpClient
        .Builder()
        .eventListener(eventListener)
        .cookieJar(cookieJar)
        .addInterceptor { chain ->
          ran += "interceptor"
          chain.proceed(
            chain
              .request()
              .newBuilder()
              .header("Authorization", "Bearer sk_from_interceptor")
              .build()
          )
        }.addNetworkInterceptor { chain ->
          ran += "network interceptor"
          chain.proceed(chain.request())
        }.authenticator { _, response ->
          ran += "authenticator"
          if (response.request.header("Authorization") != null) {
            null
          } else {
            response.request
              .newBuilder()
              .header("Authorization", "Bearer sk_from_authenticator")
              .build()
          }
        }.build()

    val error =
      assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials(httpClient = httpClient))) }

    assertEquals("invalid_client", error.code)
    assertEquals(emptyList<String>(), ran.toList())
    val exchange = exchanges().single()
    assertFalse(exchange.containsHeader("Authorization"))
    assertFalse(exchange.containsHeader("Cookie"))
    assertEquals(0, apiAttempts().size)
  }

  @Test
  fun `the exchange keeps its own redirect and retry rules with the httpClient setting`() {
    // A default OkHttpClient follows redirects, and silently resends a request
    // when the pooled connection it used fails. The exchange must do neither,
    // whichever client it is given. The first exchange leaves a connection in
    // the pool, so the reset hits a connection that has already served a call:
    // the case OkHttp resends on every machine. The numeric address keeps the
    // number of routes at one.
    stubTokenEndpoint(
      tokenSuccess("access-1"),
      aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER),
      aResponse().withStatus(307).withHeader("Location", "${elsewhere.baseUrl()}$TOKEN_PATH")
    )
    stubApi(apiOk())
    elsewhere.stubFor(any(anyUrl()).willReturn(tokenSuccess("access-from-elsewhere")))
    val providerCalls = AtomicInteger()
    val workos =
      client(
        credentials(
          provider = { "assertion-${providerCalls.incrementAndGet()}" },
          tokenUrl = "http://127.0.0.1:${api.port}$TOKEN_PATH",
          httpClient = OkHttpClient()
        )
      )
    getThings(workos)
    advanceClockTo(300)

    val error = assertThrows(WorkloadIdentityException::class.java) { getThings(workos) }

    assertEquals(WorkloadIdentityException.EXCHANGE_FAILED, error.code)
    assertEquals(3, providerCalls.get())
    assertEquals(0, elsewhere.findAll(anyRequestedFor(anyUrl())).size)
    assertEquals(1, apiAttempts().size)
  }

  @Test
  fun `an exchange that outlives its deadline fails every waiter and the next request starts a fresh exchange`() {
    stubTokens()
    stubApi(apiOk())
    val providerCalls = AtomicInteger()
    val interrupted = CountDownLatch(1)
    val provider =
      IdentityTokenProvider {
        if (providerCalls.incrementAndGet() == 1) {
          // The first call never returns by itself; only the deadline's interrupt ends it.
          try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(30))
          } catch (e: InterruptedException) {
            interrupted.countDown()
            throw e
          }
        }
        "assertion-1"
      }
    // Long enough that the second, healthy exchange is never near it on a slow machine.
    val workos = client(credentials(provider = provider, exchangeDeadlineMillis = 1_500))

    val error = assertThrows(WorkloadIdentityException::class.java) { getThings(workos) }

    assertEquals(WorkloadIdentityException.EXCHANGE_FAILED, error.code)
    assertTrue(error.description.contains("did not finish"), "unexpected description: ${error.description}")
    assertTrue(interrupted.await(5, TimeUnit.SECONDS), "the stuck exchange was not interrupted")
    assertEquals(0, exchangeCount())

    // Nothing is left behind: the next request runs its own exchange and succeeds.
    getThings(workos)

    assertEquals(2, providerCalls.get())
    assertEquals(1, exchangeCount())
    assertEquals(1, apiAttemptsWith("access-1"))
  }

  @Test
  fun `a 429 or 5xx is retried even when its body is an OAuth error and the code surfaces when retries run out`() {
    stubTokenEndpoint(
      aResponse()
        .withStatus(503)
        .withHeader("Content-Type", "application/json")
        .withBody("""{"error": "temporarily_unavailable", "error_description": "try again later"}""")
    )
    stubApi(apiOk())

    val error = assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials())) }

    assertEquals(RetryConfig.DEFAULT.maxRetries + 1, exchangeCount())
    assertEquals("temporarily_unavailable", error.code)
    assertEquals("try again later", error.description)
    assertEquals(503, error.status)
    // A code the SDK has no specific hint for still gets one.
    assertEquals("The token endpoint rejected the exchange.", error.hint)
  }

  @Test
  fun `a response that is not a token response is an invalid_response error and is not retried`() {
    val unreadable =
      listOf(
        aResponse().withStatus(200).withBody("<html>not json</html>"),
        aResponse().withStatus(200).withBody("""["access_token"]"""),
        aResponse().withStatus(200).withBody("""{"token_type": "Bearer", "expires_in": 300}"""),
        aResponse().withStatus(200).withBody("""{"access_token": "", "expires_in": 300}"""),
        aResponse().withStatus(404).withBody("not found"),
        aResponse().withStatus(400).withBody("""{"message": "no error field"}""")
      )
    for (response in unreadable) {
      api.resetAll()
      stubTokenEndpoint(response)
      stubApi(apiOk())

      val error = assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials())) }

      assertEquals(WorkloadIdentityException.INVALID_RESPONSE, error.code)
      assertEquals(1, exchangeCount())
      assertEquals(0, apiAttempts().size)
    }
  }

  @Test
  fun `an expires_in that is not a positive number is treated as 300 seconds`() {
    for (expiresIn in listOf(""""soon"""", "0", "-5", "null")) {
      api.resetAll()
      clock.set(0)
      stubTokenEndpoint(
        aResponse().withStatus(200).withBody("""{"access_token": "access-1", "expires_in": $expiresIn}"""),
        tokenSuccess("access-2")
      )
      stubApi(apiOk())
      val workos = client(credentials())

      getThings(workos)
      advanceClockTo(239)
      getThings(workos)
      assertEquals(1, exchangeCount(), "expires_in $expiresIn")

      advanceClockTo(241)
      getThings(workos)
      assertEquals(2, exchangeCount(), "expires_in $expiresIn")
    }
  }

  @Test
  fun `the description from the token endpoint is cut to 500 characters`() {
    stubTokenEndpoint(oauthError("invalid_grant", "d".repeat(900)))
    stubApi(apiOk())

    val error = assertThrows(WorkloadIdentityException::class.java) { getThings(client(credentials())) }

    assertEquals("d".repeat(500), error.description)
    assertEquals("invalid_grant: ${error.hint} ${"d".repeat(500)}", error.message)
  }

  /** Holds a response inside WireMock until the test releases it, so an exchange stays in flight on demand. */
  class HoldUntilReleased : ResponseTransformerV2 {
    @Volatile
    private var gate = CountDownLatch(0)

    @Volatile
    private var entered = CountDownLatch(0)

    /** Make the next response that uses this transformer wait for [release]. */
    fun arm() {
      gate = CountDownLatch(1)
      entered = CountDownLatch(1)
    }

    fun release() {
      gate.countDown()
    }

    /** Wait until a held response has reached the server. */
    fun awaitEntered(): Boolean = entered.await(10, TimeUnit.SECONDS)

    override fun getName(): String = NAME

    override fun applyGlobally(): Boolean = false

    override fun transform(
      response: Response,
      serveEvent: ServeEvent
    ): Response {
      entered.countDown()
      gate.await(30, TimeUnit.SECONDS)
      return response
    }

    companion object {
      const val NAME = "hold-until-released"
    }
  }

  companion object {
    private const val CLIENT_ID = "client_wif_test"
    private const val ENVIRONMENT_ID = "environment_wif_test"
    private const val TOKEN_PATH = "/oauth2/token"
    private const val API_PATH = "/things"

    /** Set on the test JVM by the Gradle test task. */
    private const val ENV_API_KEY = "sk_test_env"

    private val hold = HoldUntilReleased()

    /** The token endpoint and the WorkOS API. */
    @RegisterExtension
    @JvmField
    val api: WireMockExtension =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().extensions(hold))
        .build()

    /** A second server that must never be contacted: the target of the token endpoint's redirect. */
    @RegisterExtension
    @JvmField
    val elsewhere: WireMockExtension =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort())
        .build()
  }
}
