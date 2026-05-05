// @oagen-ignore-file
package com.workos.sso

import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.net.URL

class SSOAuthUrlsTest : TestBase() {
  private fun api() = SSO(createWorkOSClient())

  @Test
  fun `getAuthorizationUrl builds the expected path and encodes params`() {
    val url =
      api().getAuthorizationUrl(
        SSOAuthorizationUrlOptions(
          redirectUri = "https://example.com/cb",
          connection = "conn_123",
          state = "abc"
        )
      )
    val parsed = URL(url)
    assertEquals("/sso/authorize", parsed.path)
    val query = parseQuery(parsed.query)
    assertEquals("https://example.com/cb", query["redirect_uri"])
    assertEquals("conn_123", query["connection"])
    assertEquals("abc", query["state"])
    assertEquals("code", query["response_type"])
    assertEquals("client_01TEST", query["client_id"])
  }

  @Test
  fun `getAuthorizationUrl rejects a call with no selector`() {
    assertThrows(IllegalArgumentException::class.java) {
      api().getAuthorizationUrl(
        SSOAuthorizationUrlOptions(redirectUri = "https://example.com/cb")
      )
    }
  }

  @Test
  fun `getAuthorizationUrlWithPKCE produces a verifier, state, and challenge`() {
    val result =
      api().getAuthorizationUrlWithPKCE(
        SSOAuthorizationUrlOptions(
          redirectUri = "https://example.com/cb",
          connection = "conn_123"
        )
      )
    assertNotNull(result.codeVerifier)
    assertNotNull(result.state)
    val query = parseQuery(URL(result.url).query)
    assertEquals("S256", query["code_challenge_method"])
    assertNotNull(query["code_challenge"])
    assertEquals(result.state, query["state"])
  }

  @Test
  fun `getAuthorizationUrlWithPKCE produces unique verifiers`() {
    val opts = SSOAuthorizationUrlOptions(redirectUri = "https://example.com/cb", connection = "conn_123")
    val a = api().getAuthorizationUrlWithPKCE(opts)
    val b = api().getAuthorizationUrlWithPKCE(opts)
    assertNotEquals(a.codeVerifier, b.codeVerifier)
  }

  @Test
  fun `getLogoutUrl builds the logout URL with the token`() {
    val url = api().getLogoutUrl(SSOLogoutUrlOptions(token = "tok_123"))
    val parsed = URL(url)
    assertEquals("/sso/logout", parsed.path)
    assertEquals("tok_123", parseQuery(parsed.query)["token"])
  }

  @Test
  fun `getProfileAndTokenWithPKCE sends code_verifier and omits client_secret`() {
    wireMockRule.stubFor(
      com.github.tomakehurst.wiremock.client.WireMock
        .post(
          com.github.tomakehurst.wiremock.client.WireMock
            .urlPathEqualTo("/sso/token")
        ).willReturn(
          com.github.tomakehurst.wiremock.client.WireMock
            .aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                "access_token": "a.b.c",
                "token_type": "Bearer",
                "expires_in": 3600,
                "profile": {
                  "id": "prof_1",
                  "connection_id": "conn_1",
                  "connection_type": "OktaSAML",
                  "email": "u@e.com",
                  "idp_id": "ipd_1",
                  "object": "profile",
                  "organization_id": null,
                  "first_name": null,
                  "last_name": null,
                  "raw_attributes": {},
                  "role": null,
                  "groups": null,
                  "custom_attributes": {},
                  "team": null
                }
              }
              """.trimIndent()
            )
        )
    )
    val response = api().getProfileAndTokenWithPKCE("the-code", "the-verifier")
    assertEquals("a.b.c", response.accessToken)

    val requests =
      wireMockRule.findAll(
        com.github.tomakehurst.wiremock.client.WireMock
          .postRequestedFor(
            com.github.tomakehurst.wiremock.client.WireMock
              .urlPathEqualTo("/sso/token")
          )
      )
    val body = requests[0].bodyAsString
    assert(body.contains("code_verifier")) { "expected code_verifier in body: $body" }
    assert(!body.contains("client_secret")) { "client_secret must not be sent: $body" }
  }

  private fun parseQuery(query: String?): Map<String, String> {
    if (query.isNullOrEmpty()) return emptyMap()
    return query.split("&").associate {
      val (k, v) = it.split("=", limit = 2)
      java.net.URLDecoder.decode(k, Charsets.UTF_8) to java.net.URLDecoder.decode(v, Charsets.UTF_8)
    }
  }
}
