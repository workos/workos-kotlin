// @oagen-ignore-file
package com.workos.usermanagement

import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URL

class AuthKitAuthUrlsTest : TestBase() {
  private fun api(): UserManagement = UserManagement(createWorkOSClient())

  @Test
  fun `getAuthorizationUrl builds a properly-encoded URL with required params`() {
    val url =
      api().getAuthorizationUrl(
        AuthKitAuthorizationUrlOptions(
          redirectUri = "https://example.com/cb",
          provider = "authkit",
          state = "abc",
          clientId = "client_explicit"
        )
      )
    val parsed = URL(url)
    assertEquals("/user_management/authorize", parsed.path)
    val query = parseQuery(parsed.query)
    assertEquals("https://example.com/cb", query["redirect_uri"])
    assertEquals("authkit", query["provider"])
    assertEquals("abc", query["state"])
    assertEquals("client_explicit", query["client_id"])
    assertEquals("code", query["response_type"])
  }

  @Test
  fun `getAuthorizationUrl uses client default clientId when not overridden`() {
    val url =
      api().getAuthorizationUrl(
        AuthKitAuthorizationUrlOptions(
          redirectUri = "https://example.com/cb",
          provider = "authkit"
        )
      )
    val query = parseQuery(URL(url).query)
    assertEquals("client_01TEST", query["client_id"])
  }

  @Test
  fun `getAuthorizationUrl rejects a call with no provider, connection, or organization`() {
    assertThrows(IllegalArgumentException::class.java) {
      api().getAuthorizationUrl(
        AuthKitAuthorizationUrlOptions(redirectUri = "https://example.com/cb")
      )
    }
  }

  @Test
  fun `getAuthorizationUrl rejects screenHint for non-authkit providers`() {
    assertThrows(IllegalArgumentException::class.java) {
      api().getAuthorizationUrl(
        AuthKitAuthorizationUrlOptions(
          redirectUri = "https://example.com/cb",
          provider = "GoogleOAuth",
          screenHint = "sign-up"
        )
      )
    }
  }

  @Test
  fun `getAuthorizationUrlWithPKCE produces a URL plus verifier and state`() {
    val result =
      api().getAuthorizationUrlWithPKCE(
        AuthKitAuthorizationUrlOptions(
          redirectUri = "https://example.com/cb",
          provider = "authkit"
        )
      )
    assertNotNull(result.codeVerifier)
    assertNotNull(result.state)
    assertTrue(result.codeVerifier.length >= 43)
    val query = parseQuery(URL(result.url).query)
    assertEquals(result.state, query["state"])
    assertEquals("S256", query["code_challenge_method"])
    assertNotNull(query["code_challenge"])
  }

  @Test
  fun `getAuthorizationUrlWithPKCE generates a fresh verifier per call`() {
    val opts = AuthKitAuthorizationUrlOptions(redirectUri = "https://example.com/cb", provider = "authkit")
    val a = api().getAuthorizationUrlWithPKCE(opts)
    val b = api().getAuthorizationUrlWithPKCE(opts)
    assertNotEquals(a.codeVerifier, b.codeVerifier)
  }

  @Test
  fun `getLogoutUrl builds the expected path with session_id`() {
    val url = api().getLogoutUrl(AuthKitLogoutUrlOptions(sessionId = "session_123"))
    val parsed = URL(url)
    assertEquals("/user_management/sessions/logout", parsed.path)
    assertEquals("session_123", parseQuery(parsed.query)["session_id"])
  }

  @Test
  fun `getLogoutUrl includes return_to when provided`() {
    val url =
      api().getLogoutUrl(
        AuthKitLogoutUrlOptions(sessionId = "session_123", returnTo = "https://app.example.com")
      )
    val query = parseQuery(URL(url).query)
    assertEquals("https://app.example.com", query["return_to"])
  }

  private fun parseQuery(query: String?): Map<String, String> {
    if (query.isNullOrEmpty()) return emptyMap()
    return query.split("&").associate {
      val (k, v) = it.split("=", limit = 2)
      java.net.URLDecoder.decode(k, Charsets.UTF_8) to java.net.URLDecoder.decode(v, Charsets.UTF_8)
    }
  }
}
