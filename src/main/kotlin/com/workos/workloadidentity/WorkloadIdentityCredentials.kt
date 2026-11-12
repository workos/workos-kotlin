// @oagen-ignore-file
package com.workos.workloadidentity

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.workos.WorkOS
import com.workos.common.exceptions.WorkloadIdentityException
import com.workos.common.http.AttemptOutcome
import com.workos.common.http.CredentialSnapshot
import com.workos.common.http.Credentials
import com.workos.common.http.RetryConfig
import com.workos.common.http.RetryPolicy
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/** Header that carries the environment ID on every API request made with workload identity credentials. */
private const val ENVIRONMENT_HEADER = "x-url-environment-id"

private const val GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer"

private const val DEFAULT_RESOURCE = "https://api.workos.com"

private const val ENV_CLIENT_ID = "WORKOS_CLIENT_ID"

private const val ENV_TOKEN_URL = "WORKOS_WIF_TOKEN_URL"

private const val ENV_SCOPE = "WORKOS_WIF_SCOPE"

private const val ENV_ENVIRONMENT_ID = "WORKOS_ENVIRONMENT_ID"

/** Lifetime assumed when the token response has no usable `expires_in`. */
private const val DEFAULT_LIFETIME_SECONDS = 300.0

/** Longest lifetime the cache accepts, so clock arithmetic cannot overflow. */
private const val MAX_LIFETIME_SECONDS = 365.0 * 24 * 60 * 60

/** A token is refreshed once less than this, or less than [REFRESH_FRACTION] of its lifetime, remains. */
private val REFRESH_THRESHOLD_NANOS = TimeUnit.SECONDS.toNanos(60)

private const val REFRESH_FRACTION = 0.2

/** Deadline for one whole exchange: every attempt, provider call and backoff wait. */
private const val EXCHANGE_DEADLINE_MILLIS = 30_000L

private val JSON = ObjectMapper()

/**
 * Credentials that exchange a workload's identity token for a short-lived
 * WorkOS access token, so no API key has to be stored with the workload.
 *
 * ```kotlin
 * val credentials =
 *   WorkloadIdentityCredentials(
 *     identityTokenProvider = tokenFile("/var/run/secrets/tokens/workos"),
 *     clientId = "client_123",
 *     tokenUrl = "https://auth.example.com/oauth2/token",
 *     environmentId = "environment_123"
 *   )
 * val workos = WorkloadIdentity.client(credentials)
 * ```
 *
 * Settings left out fall back to the environment: `WORKOS_CLIENT_ID`,
 * `WORKOS_WIF_TOKEN_URL`, `WORKOS_ENVIRONMENT_ID` and `WORKOS_WIF_SCOPE`. A
 * value passed here always wins. The configuration cannot change after
 * construction; build a new instance to change it.
 *
 * The access token is cached in memory on this instance and refreshed on
 * demand when it nears expiry. Concurrent callers share one exchange. Two
 * instances never share a token, even with the same configuration.
 *
 * @throws WorkloadIdentityException with code `invalid_configuration` when a
 *   required setting is missing after the environment is consulted.
 */
class WorkloadIdentityCredentials internal constructor(
  private val identityTokenProvider: IdentityTokenProvider,
  clientId: String?,
  tokenUrl: String?,
  environmentId: String?,
  resource: String?,
  scope: String?,
  env: (String) -> String?,
  private val nanoTime: () -> Long,
  private val sleeper: (Long) -> Unit,
  private val exchangeDeadlineMillis: Long
) : Credentials {
  /**
   * Create credentials.
   *
   * @param identityTokenProvider returns the workload's identity token. See [tokenFile], [tokenEnv], or pass a lambda.
   * @param clientId the M2M application's client ID. Defaults to `WORKOS_CLIENT_ID`.
   * @param tokenUrl the token exchange endpoint. Defaults to `WORKOS_WIF_TOKEN_URL`; there is no built-in default.
   * @param environmentId the WorkOS environment ID. Defaults to `WORKOS_ENVIRONMENT_ID`.
   * @param resource the resource the access token is for. Defaults to `https://api.workos.com`.
   * @param scope the scope to request. Defaults to `WORKOS_WIF_SCOPE`; left out of the exchange when empty.
   */
  @JvmOverloads
  constructor(
    identityTokenProvider: IdentityTokenProvider,
    clientId: String? = null,
    tokenUrl: String? = null,
    environmentId: String? = null,
    resource: String? = null,
    scope: String? = null
  ) : this(
    identityTokenProvider = identityTokenProvider,
    clientId = clientId,
    tokenUrl = tokenUrl,
    environmentId = environmentId,
    resource = resource,
    scope = scope,
    env = { System.getenv(it) },
    nanoTime = { System.nanoTime() },
    sleeper = { Thread.sleep(it) },
    exchangeDeadlineMillis = EXCHANGE_DEADLINE_MILLIS
  )

  /** The M2M application's client ID. */
  @JvmField
  val clientId: String = required(clientId, env, "clientId", ENV_CLIENT_ID)

  /** The token exchange endpoint. */
  @JvmField
  val tokenUrl: String = required(tokenUrl, env, "tokenUrl", ENV_TOKEN_URL)

  /** The WorkOS environment ID, sent as a header on every API request. */
  @JvmField
  val environmentId: String = required(environmentId, env, "environmentId", ENV_ENVIRONMENT_ID)

  /** The resource the access token is requested for. */
  @JvmField
  val resource: String = resource?.takeIf { it.isNotBlank() } ?: DEFAULT_RESOURCE

  /** The scope requested in the exchange, or `null` when none is requested. */
  @JvmField
  val scope: String? = (scope ?: env(ENV_SCOPE))?.takeIf { it.isNotBlank() }

  private val exchangeUrl: HttpUrl =
    this.tokenUrl.toHttpUrlOrNull()
      ?: throw WorkloadIdentityException(
        WorkloadIdentityException.INVALID_CONFIGURATION,
        "tokenUrl is not a valid http or https URL (it comes from the tokenUrl setting or $ENV_TOKEN_URL)"
      )

  // The exchange has its own client: no redirects, and none of the
  // interceptors a caller may have installed on the API client. OkHttp's
  // silent retry of a failed connection is off, because every retry must go
  // through the exchange loop, which calls the provider again. The call
  // timeout bounds a single attempt; the whole exchange is bounded in
  // startExchange.
  private val httpClient: OkHttpClient =
    OkHttpClient
      .Builder()
      .followRedirects(false)
      .followSslRedirects(false)
      .retryOnConnectionFailure(false)
      .callTimeout(exchangeDeadlineMillis, TimeUnit.MILLISECONDS)
      .build()

  // The exchange belongs to this instance, not to one request, so it uses
  // the SDK's default retry values and never a per-request override.
  private val retryPolicy = RetryPolicy(RetryConfig.DEFAULT)

  private val userAgent = "workos-kotlin/${sdkVersion()}"

  // Everything below is guarded by `lock`. Nothing is shared between
  // instances.
  private val lock = Any()
  private var token: String? = null
  private var lifetimeNanos = 0L
  private var expiresAtNanos = 0L
  private var generation = 0L
  private var inflight: CompletableFuture<CachedToken>? = null

  override fun resolve(timeoutMillis: Long?): CredentialSnapshot {
    val current = currentToken(timeoutMillis)
    return CredentialSnapshot(
      headers =
        mapOf(
          "Authorization" to "Bearer ${current.token}",
          ENVIRONMENT_HEADER to environmentId
        ),
      generation = current.generation,
      recoverable = true
    )
  }

  override fun invalidate(generation: Long) {
    synchronized(lock) {
      // Conditional on the generation: a late 401 for an old token must not
      // discard a newer one that another request already fetched.
      if (this.generation == generation) {
        expiresAtNanos = nanoTime()
      }
    }
  }

  private fun currentToken(timeoutMillis: Long?): CachedToken {
    val call: CompletableFuture<CachedToken>
    synchronized(lock) {
      val cached = token
      if (cached != null && expiresAtNanos - nanoTime() >= refreshThresholdNanos(lifetimeNanos)) {
        return CachedToken(cached, generation)
      }
      call = inflight ?: startExchange()
    }
    return await(call, timeoutMillis)
  }

  /**
   * Wait for the shared exchange on the caller's own terms. A caller that
   * times out or is interrupted stops waiting and leaves [call] untouched,
   * so the exchange keeps running for everyone else.
   */
  private fun await(
    call: CompletableFuture<CachedToken>,
    timeoutMillis: Long?
  ): CachedToken =
    try {
      if (timeoutMillis == null) call.get() else call.get(timeoutMillis, TimeUnit.MILLISECONDS)
    } catch (e: ExecutionException) {
      throw e.cause ?: e
    }

  /**
   * Start the one exchange for this instance. Called with [lock] held.
   *
   * The exchange runs on its own daemon thread under its own deadline. It is
   * not tied to the caller that happened to start it.
   */
  private fun startExchange(): CompletableFuture<CachedToken> {
    val call = CompletableFuture<CachedToken>()
    inflight = call

    val work = CompletableFuture<TokenResponse>()
    val worker =
      Thread(
        {
          try {
            work.complete(exchange())
          } catch (e: Throwable) {
            work.completeExceptionally(e)
          }
        },
        "workos-workload-identity-exchange"
      )
    worker.isDaemon = true

    work
      .orTimeout(exchangeDeadlineMillis, TimeUnit.MILLISECONDS)
      .whenComplete { response, error ->
        if (error is TimeoutException) worker.interrupt()
        settle(call, response, error)
      }

    try {
      worker.start()
    } catch (e: Throwable) {
      work.completeExceptionally(e)
    }
    return call
  }

  /**
   * Finish the exchange: update the cache, clear the in-flight state, then
   * wake the waiters. The in-flight state is always cleared before the
   * waiters are woken, so a caller that gets an error and calls again at
   * once starts a new exchange instead of joining the finished one.
   */
  private fun settle(
    call: CompletableFuture<CachedToken>,
    response: TokenResponse?,
    error: Throwable?
  ) {
    if (response == null) {
      synchronized(lock) {
        if (inflight === call) inflight = null
      }
      call.completeExceptionally(asWorkloadIdentityException(error))
      return
    }
    val stored: CachedToken
    synchronized(lock) {
      token = response.accessToken
      lifetimeNanos = response.lifetimeNanos
      expiresAtNanos = nanoTime() + response.lifetimeNanos
      generation += 1
      stored = CachedToken(response.accessToken, generation)
      if (inflight === call) inflight = null
    }
    call.complete(stored)
  }

  private fun asWorkloadIdentityException(error: Throwable?): WorkloadIdentityException =
    when (error) {
      is WorkloadIdentityException -> error
      is TimeoutException ->
        WorkloadIdentityException(
          WorkloadIdentityException.EXCHANGE_FAILED,
          "The exchange did not finish within $exchangeDeadlineMillis ms."
        )
      else ->
        WorkloadIdentityException(
          WorkloadIdentityException.EXCHANGE_FAILED,
          "The exchange stopped unexpectedly.",
          cause = error
        )
    }

  /**
   * Run the exchange, retrying transport failures, 429 and 5xx responses and
   * provider failures with the SDK's own backoff. Every attempt calls the
   * provider again and rebuilds the form.
   */
  private fun exchange(): TokenResponse {
    val deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(exchangeDeadlineMillis)
    var attempt = 0
    while (true) {
      val retry =
        when (val result = attemptExchange()) {
          is AttemptResult.Success -> return result.response
          is AttemptResult.Retry -> result
        }
      val delayMillis = retryPolicy.nextDelay(attempt, retry.outcome) ?: throw retry.failure
      val remainingMillis = TimeUnit.NANOSECONDS.toMillis(deadlineNanos - System.nanoTime())
      if (delayMillis >= remainingMillis) throw retry.failure
      sleeper(delayMillis)
      attempt += 1
    }
  }

  /** One attempt. Returns what to do next; throws for failures that are final. */
  private fun attemptExchange(): AttemptResult {
    val assertion: String? =
      try {
        identityTokenProvider.getToken()
      } catch (e: InterruptedException) {
        throw e
      } catch (e: Exception) {
        return AttemptResult.Retry(AttemptOutcome.TransportFailure(e), providerFailed(e))
      }
    if (assertion.isNullOrBlank()) {
      val empty = IllegalStateException("The identity token provider returned an empty token.")
      return AttemptResult.Retry(AttemptOutcome.TransportFailure(empty), providerFailed(empty))
    }

    val form =
      FormBody
        .Builder()
        .add("grant_type", GRANT_TYPE)
        .add("assertion", assertion)
        .add("client_id", clientId)
        .add("resource", resource)
    scope?.let { form.add("scope", it) }
    val request =
      Request
        .Builder()
        .url(exchangeUrl)
        .post(form.build())
        .header("Accept", "application/json")
        .header("User-Agent", userAgent)
        .build()

    val reply =
      try {
        httpClient.newCall(request).execute().use { response ->
          TokenEndpointReply(response.code, response.header("Retry-After"), response.body.string())
        }
      } catch (e: IOException) {
        // An I/O failure names the host at most and never holds the form,
        // so it is safe to keep as the cause.
        val failure =
          WorkloadIdentityException(
            WorkloadIdentityException.EXCHANGE_FAILED,
            "The request to the token endpoint failed: ${e.javaClass.simpleName}.",
            cause = e
          )
        return AttemptResult.Retry(AttemptOutcome.TransportFailure(e), failure)
      }
    val status = reply.status
    val body = reply.body

    // The status is checked before the body: a 429 or 5xx is retried even
    // when its body is an OAuth error.
    val oauthError = parseOAuthError(body)
    if (RetryPolicy.isRetryableStatus(status)) {
      val failure =
        WorkloadIdentityException(
          oauthError?.code ?: WorkloadIdentityException.EXCHANGE_FAILED,
          oauthError?.description?.takeIf { it.isNotEmpty() } ?: "The token endpoint returned HTTP $status.",
          status
        )
      return AttemptResult.Retry(AttemptOutcome.Response(status, reply.retryAfter), failure)
    }
    if (oauthError != null) {
      throw WorkloadIdentityException(oauthError.code, oauthError.description, status)
    }
    if (status in 300..399) {
      throw WorkloadIdentityException(
        WorkloadIdentityException.EXCHANGE_FAILED,
        "The token endpoint answered with a redirect (HTTP $status), which is not followed.",
        status
      )
    }
    if (status != 200) {
      throw WorkloadIdentityException(
        WorkloadIdentityException.INVALID_RESPONSE,
        "The token endpoint returned HTTP $status.",
        status
      )
    }
    return AttemptResult.Success(parseTokenResponse(body))
  }

  private fun providerFailed(cause: Throwable): WorkloadIdentityException =
    WorkloadIdentityException(
      WorkloadIdentityException.PROVIDER_FAILED,
      cause.message ?: cause.javaClass.simpleName,
      cause = cause
    )

  private fun parseOAuthError(body: String): OAuthError? {
    val tree = readJson(body) ?: return null
    val code =
      tree
        .path("error")
        .takeIf { it.isTextual }
        ?.asText()
        ?.takeIf { it.isNotEmpty() } ?: return null
    val description =
      tree
        .path("error_description")
        .takeIf { it.isTextual }
        ?.asText()
        .orEmpty()
    return OAuthError(code, description)
  }

  private fun parseTokenResponse(body: String): TokenResponse {
    val tree =
      readJson(body)
        ?: throw WorkloadIdentityException(
          WorkloadIdentityException.INVALID_RESPONSE,
          "The token endpoint returned HTTP 200 with a body that is not a JSON object.",
          200
        )
    val accessToken = tree.path("access_token").takeIf { it.isTextual }?.asText()
    if (accessToken.isNullOrEmpty()) {
      throw WorkloadIdentityException(
        WorkloadIdentityException.INVALID_RESPONSE,
        "The token response has no access_token.",
        200
      )
    }
    val expiresIn = tree.path("expires_in")
    val lifetimeSeconds =
      if (expiresIn.isNumber && expiresIn.asDouble() > 0) expiresIn.asDouble() else DEFAULT_LIFETIME_SECONDS
    val lifetimeNanos = (lifetimeSeconds.coerceAtMost(MAX_LIFETIME_SECONDS) * 1e9).toLong()
    return TokenResponse(accessToken, lifetimeNanos)
  }

  private fun readJson(body: String): JsonNode? =
    try {
      JSON.readTree(body)?.takeIf { it.isObject }
    } catch (_: JsonProcessingException) {
      null
    }

  /**
   * Fluent builder for [WorkloadIdentityCredentials], for Java callers who
   * cannot use Kotlin named arguments.
   *
   * ```java
   * WorkloadIdentityCredentials credentials = WorkloadIdentityCredentials.builder()
   *     .identityTokenProvider(IdentityTokenProviders.tokenFile("/var/run/secrets/tokens/workos"))
   *     .clientId("client_123")
   *     .tokenUrl("https://auth.example.com/oauth2/token")
   *     .environmentId("environment_123")
   *     .build();
   * ```
   */
  class Builder {
    private var identityTokenProvider: IdentityTokenProvider? = null
    private var clientId: String? = null
    private var tokenUrl: String? = null
    private var environmentId: String? = null
    private var resource: String? = null
    private var scope: String? = null

    /** Set the provider that returns the workload's identity token (required). */
    fun identityTokenProvider(provider: IdentityTokenProvider): Builder = apply { identityTokenProvider = provider }

    /** Set the M2M application's client ID. Defaults to `WORKOS_CLIENT_ID`. */
    fun clientId(clientId: String): Builder = apply { this.clientId = clientId }

    /** Set the token exchange endpoint. Defaults to `WORKOS_WIF_TOKEN_URL`. */
    fun tokenUrl(tokenUrl: String): Builder = apply { this.tokenUrl = tokenUrl }

    /** Set the WorkOS environment ID. Defaults to `WORKOS_ENVIRONMENT_ID`. */
    fun environmentId(environmentId: String): Builder = apply { this.environmentId = environmentId }

    /** Set the resource the access token is for. Defaults to `https://api.workos.com`. */
    fun resource(resource: String): Builder = apply { this.resource = resource }

    /** Set the scope to request. Defaults to `WORKOS_WIF_SCOPE`. */
    fun scope(scope: String): Builder = apply { this.scope = scope }

    /**
     * Build the credentials. Throws [WorkloadIdentityException] with code
     * `invalid_configuration` when a required setting is missing.
     */
    fun build(): WorkloadIdentityCredentials {
      val provider =
        identityTokenProvider
          ?: throw WorkloadIdentityException(
            WorkloadIdentityException.INVALID_CONFIGURATION,
            "identityTokenProvider is required"
          )
      return WorkloadIdentityCredentials(provider, clientId, tokenUrl, environmentId, resource, scope)
    }
  }

  /** Factory for obtaining a [Builder]. */
  companion object {
    /** Create a new [Builder]. */
    @JvmStatic
    fun builder(): Builder = Builder()
  }
}

/** A cached access token with the generation that identifies it. */
private class CachedToken(
  val token: String,
  val generation: Long
)

/** A successful token response, reduced to what the cache keeps. */
private class TokenResponse(
  val accessToken: String,
  val lifetimeNanos: Long
)

private class TokenEndpointReply(
  val status: Int,
  val retryAfter: String?,
  val body: String
)

private class OAuthError(
  val code: String,
  val description: String
)

private sealed class AttemptResult {
  class Success(
    val response: TokenResponse
  ) : AttemptResult()

  /** The attempt failed in a way worth retrying; [failure] is what to raise if no retries are left. */
  class Retry(
    val outcome: AttemptOutcome,
    val failure: WorkloadIdentityException
  ) : AttemptResult()
}

private fun refreshThresholdNanos(lifetimeNanos: Long): Long = minOf(REFRESH_THRESHOLD_NANOS, (lifetimeNanos * REFRESH_FRACTION).toLong())

private fun required(
  explicit: String?,
  env: (String) -> String?,
  field: String,
  variable: String
): String =
  explicit?.takeIf { it.isNotBlank() }
    ?: env(variable)?.takeIf { it.isNotBlank() }
    ?: throw WorkloadIdentityException(
      WorkloadIdentityException.INVALID_CONFIGURATION,
      "$field is required (or set $variable)"
    )

private fun sdkVersion(): String {
  val resource = WorkOS::class.java.getResourceAsStream("/version.properties") ?: return "dev"
  return resource.use { stream ->
    java.util
      .Properties()
      .apply { load(stream) }
      .getProperty("version", "dev")
  }
}
