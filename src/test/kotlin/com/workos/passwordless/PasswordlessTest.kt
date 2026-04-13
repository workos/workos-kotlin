// @oagen-ignore-file
package com.workos.passwordless

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.workos.common.exceptions.UnauthorizedException
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PasswordlessTest : TestBase() {
  private fun api() = Passwordless(createWorkOSClient())

  @Test
  fun `createSession posts the options and returns a typed session`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/passwordless/sessions"))
        .withRequestBody(
          equalToJson(
            """
            {
              "type": "MagicLink",
              "email": "user@example.com",
              "redirect_uri": "https://example.com/cb",
              "expires_in": 600
            }
            """.trimIndent()
          )
        ).willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                "id": "passwordless_session_1",
                "email": "user@example.com",
                "expires_at": "2024-01-01T00:00:00Z",
                "link": "https://example.com/magic",
                "object": "passwordless_session"
              }
              """.trimIndent()
            )
        )
    )
    val result =
      api().createSession(
        CreatePasswordlessSessionOptions(
          email = "user@example.com",
          redirectUri = "https://example.com/cb",
          expiresIn = 600
        )
      )
    assertEquals("passwordless_session_1", result.id)
    assertEquals("https://example.com/magic", result.link)
  }

  @Test
  fun `sendSession posts to the sessions-id-send path`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/passwordless/sessions/session_123/send"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"success": true}""")
        )
    )
    val result = api().sendSession("session_123")
    assertTrue(result.success == true)
  }

  @Test
  fun `createSession maps 401 to UnauthorizedException`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/passwordless/sessions"))
        .willReturn(aResponse().withStatus(401).withBody("{}"))
    )
    assertThrows(UnauthorizedException::class.java) {
      api().createSession(CreatePasswordlessSessionOptions(email = "u@x.com"))
    }
  }

  @Test
  fun `passwordless accessor is cached on the WorkOS client`() {
    val workos = createWorkOSClient()
    assertSame(workos.passwordless, workos.passwordless)
  }
}
