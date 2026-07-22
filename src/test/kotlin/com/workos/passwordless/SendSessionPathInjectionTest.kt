// @oagen-ignore-file
package com.workos.passwordless

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Regression guard for the path-injection fix (SEC-1236): a caller-supplied
 * session id must be percent-encoded so reserved characters (`/`, `?`, `#`)
 * and dot-segments cannot escape the `/passwordless/sessions/{id}/send`
 * segment and redirect the API-key-authenticated POST to another endpoint.
 */
class SendSessionPathInjectionTest : TestBase() {
  private fun api() = Passwordless(createWorkOSClient())

  private fun captureUrlFor(sessionId: String): String {
    wireMockRule.resetAll()
    wireMockRule.stubFor(
      any(anyUrl()).willReturn(
        aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("""{"success": true}""")
      )
    )
    api().sendSession(sessionId)
    return wireMockRule.findAll(RequestPatternBuilder.allRequests()).single().url
  }

  @Test
  fun `legit session id maps to the send path`() {
    assertEquals("/passwordless/sessions/session_123/send", captureUrlFor("session_123"))
  }

  @Test
  fun `malicious session id stays inside its path segment`() {
    val url = captureUrlFor("../../user_management/invitations/inv_VICTIM/accept#")
    assertEquals(
      "/passwordless/sessions/..%2F..%2Fuser_management%2Finvitations%2Finv_VICTIM%2Faccept%23/send",
      url
    )
  }
}
