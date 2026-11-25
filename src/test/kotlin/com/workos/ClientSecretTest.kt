// @oagen-ignore-file
package com.workos

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.workos.common.http.RetryConfig
import com.workos.common.json.ObjectMapperFactory
import com.workos.session.Iron
import com.workos.session.RefreshSessionFailureReason
import com.workos.session.RefreshSessionResult
import com.workos.session.SessionCookieData
import com.workos.session.session
import com.workos.test.TestBase
import com.workos.workloadidentity.WorkloadIdentity
import com.workos.workloadidentity.WorkloadIdentityCredentials
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Which clients may send the API key as `client_secret`. A client built with
 * a key and a public client behave as before. A client that authenticates
 * with workload identity credentials has no key, and is refused before a
 * request is sent and before an access token is fetched.
 */
class ClientSecretTest : TestBase() {
  private val cookiePassword = "this-is-at-least-thirty-two-chars!"
  private val mapper = ObjectMapperFactory.create()
  private val providerCalls = AtomicInteger()

  private fun http(): OkHttpClient = OkHttpClient.Builder().callTimeout(5, TimeUnit.SECONDS).build()

  private fun publicClient(): PublicClient =
    PublicClient.create(clientId = "client_01TEST", apiBaseUrl = wireMockRule.baseUrl(), httpClient = http())

  /**
   * A client that authenticates with workload identity credentials. Every token exchange starts by calling the
   * identity token provider, so the provider counts its calls, and it fails so that an exchange never reaches
   * the network. The sleeper does not wait, which keeps a failing run short.
   */
  private fun workloadIdentityClient(): WorkOS {
    val credentials =
      WorkloadIdentityCredentials(
        identityTokenProvider = {
          providerCalls.incrementAndGet()
          error("no token exchange is expected")
        },
        clientId = "client_wif_test",
        tokenUrl = "https://token.invalid/oauth2/token",
        environmentId = null,
        resource = null,
        scope = null,
        httpClient = null,
        nanoTime = { System.nanoTime() },
        sleeper = {},
        exchangeDeadlineMillis = 30_000
      )
    return WorkloadIdentity.client(
      credentials = credentials,
      clientId = "client_01TEST",
      apiBaseUrl = wireMockRule.baseUrl(),
      httpClient = http(),
      retryConfig = RetryConfig.DISABLED
    )
  }

  /** The error a workload identity client gets, written out in full so a change of wording fails here. */
  private fun refusal(method: String): String =
    "$method needs an API key: this request sends it as the client secret. " +
      "A client that authenticates with workload identity has no API key, " +
      "so use a client built with one for this call."

  /** A sealed session cookie that unseals, so a refresh gets as far as the request. */
  private fun sealedSession(): String =
    Iron.seal(
      mapper.writeValueAsString(SessionCookieData(accessToken = "old.access.token", refreshToken = "refresh_old")),
      cookiePassword
    )

  private fun assertNothingWasSent() {
    assertEquals(0, providerCalls.get(), "no token exchange may start")
    assertTrue(wireMockRule.findAll(anyRequestedFor(anyUrl())).isEmpty(), "no request may reach the API")
  }

  private fun stubAuthenticate() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                "user": {
                  "object": "user",
                  "id": "u_1",
                  "email": "u@e.com",
                  "first_name": null,
                  "last_name": null,
                  "email_verified": true,
                  "profile_picture_url": null,
                  "last_sign_in_at": null,
                  "created_at": "2024-01-01T00:00:00Z",
                  "updated_at": "2024-01-01T00:00:00Z",
                  "external_id": null,
                  "metadata": {}
                },
                "access_token": "a.b.c",
                "refresh_token": "r"
              }
              """.trimIndent()
            )
        )
    )
  }

  @Test
  fun `clientSecret returns the API key of a client built with one`() {
    assertEquals("sk_test_example", createWorkOSClient().clientSecret("authenticateWithPassword"))
  }

  @Test
  fun `clientSecret returns the empty key of a client with no key and no credentials`() {
    assertEquals("", publicClient().workos.clientSecret("getProfileAndToken"))
    assertEquals("", WorkOS(apiKey = "").clientSecret("getProfileAndToken"))
  }

  @Test
  fun `clientSecret refuses a workload identity client and names the method`() {
    val workos = workloadIdentityClient()

    val error = assertThrows(IllegalStateException::class.java) { workos.clientSecret("authenticateWithPassword") }

    assertEquals(IllegalStateException::class.java, error.javaClass)
    assertEquals(refusal("authenticateWithPassword"), error.message)
    assertNothingWasSent()
  }

  @Test
  fun `session refresh is refused for a workload identity client before anything is sent`() {
    val workos = workloadIdentityClient()

    val fromHelper =
      assertThrows(IllegalStateException::class.java) {
        workos.session.loadSealedSession(sealedSession(), cookiePassword).refresh()
      }
    val fromShortcut =
      assertThrows(IllegalStateException::class.java) {
        workos.session.refreshSession(sealedSession(), cookiePassword)
      }

    assertEquals(refusal("refresh"), fromHelper.message)
    assertEquals(refusal("refresh"), fromShortcut.message)
    assertNothingWasSent()
  }

  @Test
  fun `session refresh is still sent for a client with no key and no credentials`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .willReturn(
          aResponse()
            .withStatus(400)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"code": "invalid_grant", "message": "The refresh token is not valid."}""")
        )
    )
    val workos =
      WorkOS(
        apiKey = "",
        clientId = "client_01TEST",
        apiBaseUrl = wireMockRule.baseUrl(),
        httpClient = http(),
        retryConfig = RetryConfig.DISABLED
      )

    val result = workos.session.refreshSession(sealedSession(), cookiePassword)

    // The API's answer comes back: the request was made, as before.
    assertEquals(RefreshSessionResult.Failure(RefreshSessionFailureReason.INVALID_GRANT), result)
    assertEquals(1, wireMockRule.findAll(postRequestedFor(urlPathEqualTo("/user_management/authenticate"))).size)
  }

  @Test
  fun `a public client still exchanges a code with PKCE`() {
    stubAuthenticate()
    val public = publicClient()

    // The PKCE helper sends no client secret at all.
    val fromHelper = public.authenticateWithCode("the-code", "the-verifier")
    // The generated method fills client_secret from the client, which has an empty key.
    val fromGenerated = public.workos.userManagement.authenticateWithCode("the-code", codeVerifier = "the-verifier")

    assertEquals("a.b.c", fromHelper.accessToken)
    assertEquals("a.b.c", fromGenerated.accessToken)
    val requests = wireMockRule.findAll(postRequestedFor(urlPathEqualTo("/user_management/authenticate")))
    assertEquals(2, requests.size)
    for (request in requests) {
      val body = mapper.readTree(request.bodyAsString)
      assertEquals("the-code", body.path("code").asText())
      assertEquals("the-verifier", body.path("code_verifier").asText())
      assertEquals("", body.path("client_secret").asText(""), "a public client has no secret to send")
      assertFalse(request.containsHeader("Authorization"), "a public client has no bearer token to send")
    }
  }
}
