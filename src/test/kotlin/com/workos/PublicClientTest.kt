// @oagen-ignore-file
package com.workos

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.workos.sso.SSOAuthorizationUrlOptions
import com.workos.test.TestBase
import com.workos.usermanagement.AuthKitAuthorizationUrlOptions
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.concurrent.TimeUnit

class PublicClientTest : TestBase() {
  private fun client(): PublicClient =
    PublicClient.create(
      clientId = "client_01TEST",
      apiBaseUrl = wireMockRule.baseUrl(),
      httpClient = OkHttpClient.Builder().callTimeout(5, TimeUnit.SECONDS).build()
    )

  @Test
  fun `PublicClient create requires a non-empty clientId`() {
    assertThrows(IllegalArgumentException::class.java) {
      PublicClient.create(clientId = "")
    }
  }

  @Test
  fun `getAuthorizationUrl delegates to the AuthKit URL builder`() {
    val url =
      client().getAuthorizationUrl(
        AuthKitAuthorizationUrlOptions(redirectUri = "https://ex.com/cb", provider = "authkit")
      )
    val parsed = URL(url)
    assertEquals("/user_management/authorize", parsed.path)
    assertTrue(parsed.query.contains("client_id=client_01TEST"))
  }

  @Test
  fun `getAuthorizationUrlWithPKCE returns a state and code verifier`() {
    val result =
      client().getAuthorizationUrlWithPKCE(
        AuthKitAuthorizationUrlOptions(redirectUri = "https://ex.com/cb", provider = "authkit")
      )
    assertNotNull(result.state)
    assertTrue(result.codeVerifier.length >= 43)
  }

  @Test
  fun `getSSOAuthorizationUrl delegates to the SSO URL builder`() {
    val url =
      client().getSSOAuthorizationUrl(
        SSOAuthorizationUrlOptions(redirectUri = "https://ex.com/cb", connection = "conn_1")
      )
    val parsed = URL(url)
    assertEquals("/sso/authorize", parsed.path)
  }

  @Test
  fun `authenticateWithCode sends code_verifier and never sends client_secret`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .withRequestBody(matchingJsonPath("$.code", equalTo("the-code")))
        .withRequestBody(matchingJsonPath("$.code_verifier", equalTo("the-verifier")))
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
    val response = client().authenticateWithCode("the-code", "the-verifier")
    assertEquals("a.b.c", response.accessToken)

    // Verify client_secret was NOT sent.
    val requests =
      wireMockRule.findAll(
        com.github.tomakehurst.wiremock.client.WireMock
          .postRequestedFor(urlPathEqualTo("/user_management/authenticate"))
      )
    assertTrue(requests.isNotEmpty())
    val body = requests[0].bodyAsString
    assertTrue(!body.contains("client_secret")) {
      "client_secret must not be sent on public-client code exchange; got: $body"
    }
  }
}
