// @oagen-ignore-file
package com.workos.workloadidentity.githubactions

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException

/** Conformance cases WIF-GHA-01 and WIF-GHA-02, against a local mock of the runner's OIDC endpoint. */
class WIFGitHubActionsTest {
  private fun environment(
    url: String? = "${runner.baseUrl()}/token?api-version=2.0",
    requestToken: String? = "gha-request-token"
  ): (String) -> String? =
    { name ->
      when (name) {
        "ACTIONS_ID_TOKEN_REQUEST_URL" -> url
        "ACTIONS_ID_TOKEN_REQUEST_TOKEN" -> requestToken
        else -> null
      }
    }

  private fun stubToken(body: String) {
    runner.stubFor(
      get(urlPathEqualTo("/token")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(body))
    )
  }

  @Test
  fun `WIF-GHA-01 gets the request URL with the encoded audience appended and the request token as a bearer`() {
    stubToken("""{"count": 1337, "value": "gha-jwt"}""")

    val token = githubActions("https://api.workos.com", environment()).getToken()

    assertEquals("gha-jwt", token)
    val request = runner.findAll(anyRequestedFor(anyUrl())).single()
    assertEquals("GET", request.method.value())
    // The existing query string is kept and the audience is added to it, percent-encoded.
    assertEquals("/token?api-version=2.0&audience=https%3A%2F%2Fapi.workos.com", request.url)
    assertEquals("Bearer gha-request-token", request.getHeader("Authorization"))
    assertEquals("application/json", request.getHeader("Accept"))
  }

  @Test
  fun `WIF-GHA-01 a request URL with no query string gets one`() {
    stubToken("""{"value":"gha-jwt"}""")

    val token = githubActions("urn:workos:audience/one", environment(url = "${runner.baseUrl()}/token")).getToken()

    assertEquals("gha-jwt", token)
    assertEquals("/token?audience=urn%3Aworkos%3Aaudience%2Fone", runner.findAll(anyRequestedFor(anyUrl())).single().url)
  }

  @Test
  fun `a space in the audience is percent-encoded and not written as a plus sign`() {
    stubToken("""{"value":"gha-jwt"}""")

    githubActions("my audience+1", environment(url = "${runner.baseUrl()}/token")).getToken()

    assertEquals("/token?audience=my%20audience%2B1", runner.findAll(anyRequestedFor(anyUrl())).single().url)
  }

  @Test
  fun `WIF-GHA-02 missing environment variables fail with the hint to add the permission`() {
    stubToken("""{"value": "gha-jwt"}""")

    for (env in listOf(environment(url = null), environment(requestToken = null), environment(url = ""), environment(requestToken = ""))) {
      val error = assertThrows(IllegalStateException::class.java) { githubActions("https://api.workos.com", env).getToken() }
      assertTrue(error.message.orEmpty().contains("permissions: id-token: write"), "unexpected message: ${error.message}")
    }

    assertEquals(0, runner.findAll(anyRequestedFor(anyUrl())).size)
  }

  @Test
  fun `a failing endpoint is an error that names the status and never the request token`() {
    runner.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(404).withBody("""{"value": "not-a-token"}""")))

    val error = assertThrows(IOException::class.java) { githubActions("https://api.workos.com", environment()).getToken() }

    assertTrue(error.message.orEmpty().contains("404"))
    assertFalse(error.message.orEmpty().contains("gha-request-token"))
  }

  @Test
  fun `a response with no value and a redirect are errors and the redirect is not followed`() {
    stubToken("""{"count": 1}""")
    assertThrows(IOException::class.java) { githubActions("https://api.workos.com", environment()).getToken() }

    stubToken("""{"value": ""}""")
    assertThrows(IOException::class.java) { githubActions("https://api.workos.com", environment()).getToken() }

    runner.resetAll()
    runner.stubFor(
      get(urlPathEqualTo("/token")).willReturn(aResponse().withStatus(302).withHeader("Location", "${runner.baseUrl()}/elsewhere"))
    )
    runner.stubFor(get(urlPathEqualTo("/elsewhere")).willReturn(aResponse().withStatus(200).withBody("""{"value": "redirected"}""")))
    assertThrows(IOException::class.java) { githubActions("https://api.workos.com", environment()).getToken() }
    assertEquals(0, runner.findAll(anyRequestedFor(urlPathEqualTo("/elsewhere"))).size)
  }

  @Test
  fun `the value field is read from the JSON response with escapes resolved`() {
    assertEquals("a.b.c", tokenFrom("""{"count":1,"value":"a.b.c"}"""))
    assertEquals("a.b.c", tokenFrom("{\n  \"value\" : \"a.b.c\"\n}"))
    assertEquals("quote\" slash/ back\\ é", tokenFrom("""{"value": "quote\" slash\/ back\\ é"}"""))
    assertEquals("A", tokenFrom("""{"value": "\u0041"}"""))
    // A string that says "value" is not the key; the key that follows is.
    assertEquals("t", tokenFrom("""{"kind":"value","value":"t"}"""))
    assertNull(tokenFrom("""{"count": 1}"""))
    assertNull(tokenFrom("""{"value": 12}"""))
    assertNull(tokenFrom(""))
    // A string that is never closed, and escapes that are cut short.
    assertNull(tokenFrom("""{"value":"abc"""))
    assertNull(tokenFrom("""{"value":"abc\"""))
    assertNull(tokenFrom("""{"value":"abc\u00"""))
  }

  @Test
  fun `a token as long as a real one is read on a thread with a small stack`() {
    // A real GitHub OIDC token is about 2,000 characters. Reading it must not depend on the stack size.
    val token = "eyJ" + "a".repeat(16_000)
    val result = arrayOfNulls<Any>(1)
    val reader =
      Thread(
        null,
        {
          result[0] =
            try {
              tokenFrom("""{"count": 1, "value": "$token"}""")
            } catch (e: Throwable) {
              e
            }
        },
        "small-stack",
        256 * 1024
      )

    reader.start()
    reader.join(10_000)

    assertEquals(token, result[0])
  }

  @Test
  fun `an empty audience is rejected when the provider is built`() {
    assertThrows(IllegalArgumentException::class.java) { githubActions(" ") }
  }

  companion object {
    /** Stands in for the workflow run's OIDC endpoint. */
    @RegisterExtension
    @JvmField
    val runner: WireMockExtension =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort())
        .build()
  }
}
