// @oagen-ignore-file
package com.workos.common.http

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.workos.common.exceptions.BadRequestException
import com.workos.common.exceptions.GenericException
import com.workos.common.exceptions.GenericServerException
import com.workos.common.exceptions.NotFoundException
import com.workos.common.exceptions.RateLimitException
import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.UnprocessableEntityException
import com.workos.common.exceptions.WorkOSException
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Shared error-translation suite for [BaseClient].  This replaces the
 * per-resource 4xx/5xx duplicate coverage — regression tests for resource
 * behavior can rely on this suite to confirm that each status code is
 * mapped to the right `WorkOSException` subclass with its wire metadata.
 *
 * When adding a new HTTP status, extend this suite rather than adding a
 * new per-service error test.
 */
class BaseClientErrorMappingTest : TestBase() {
  private fun makeRequest(): RequestConfig =
    RequestConfig(
      method = "GET",
      path = "/errors",
      queryParams = emptyList()
    )

  private inline fun <reified T : WorkOSException> runErrorCase(
    status: Int,
    body: String,
    extraHeaders: Map<String, String> = emptyMap()
  ): T {
    val builder =
      aResponse()
        .withStatus(status)
        .withHeader("Content-Type", "application/json")
        .withHeader("X-Request-Id", "req_01TEST")
        .withBody(body)
    extraHeaders.forEach { (k, v) -> builder.withHeader(k, v) }
    wireMockRule.stubFor(get(urlPathEqualTo("/errors")).willReturn(builder))

    val client = createWorkOSClient()
    val thrown =
      assertThrows(T::class.java) {
        client.baseClient.request(makeRequest(), Map::class.java)
      }
    assertEquals("req_01TEST", thrown.requestId)
    return thrown
  }

  @Test
  fun `400 maps to BadRequestException with parsed code, message, errors`() {
    val body =
      """{"code": "bad_request", "message": "Something broke",
         "errors": [{"code": "required", "field": "slug"}]}"""
    val ex = runErrorCase<BadRequestException>(400, body)
    assertEquals("bad_request", ex.code)
    assertEquals("Something broke", ex.message)
    val errors = ex.errors
    assertNotNull(errors)
    assertEquals(1, errors!!.size)
    assertEquals("required", errors[0]["code"])
    assertEquals("slug", errors[0]["field"])
  }

  @Test
  fun `401 maps to UnauthorizedException`() {
    val ex = runErrorCase<UnauthorizedException>(401, """{"message": "No API key"}""")
    assertEquals("No API key", ex.message)
  }

  @Test
  fun `403 falls through to GenericException with 403 status`() {
    val ex = runErrorCase<GenericException>(403, """{"message": "Forbidden"}""")
    assertEquals(403, ex.status)
  }

  @Test
  fun `404 maps to NotFoundException`() {
    val ex = runErrorCase<NotFoundException>(404, """{"message": "Not here"}""")
    assertEquals("Not here", ex.message)
  }

  @Test
  fun `409 falls through to GenericException with 409 status`() {
    val ex = runErrorCase<GenericException>(409, """{"message": "Conflict"}""")
    assertEquals(409, ex.status)
  }

  @Test
  fun `422 maps to UnprocessableEntityException with structured errors`() {
    val body =
      """{"code": "invalid", "message": "Invalid",
         "errors": [{"code": "format", "field": "email"}]}"""
    val ex = runErrorCase<UnprocessableEntityException>(422, body)
    assertEquals("invalid", ex.code)
    val errors = ex.errors
    assertNotNull(errors)
    assertEquals(1, errors!!.size)
    assertEquals("email", errors[0]["field"])
  }

  @Test
  fun `429 maps to RateLimitException and propagates numeric Retry-After`() {
    // Disable retries so the 429 surfaces as an exception instead of retrying.
    wireMockRule.stubFor(
      get(urlPathEqualTo("/errors"))
        .willReturn(
          aResponse()
            .withStatus(429)
            .withHeader("Content-Type", "application/json")
            .withHeader("X-Request-Id", "req_01TEST")
            .withHeader("Retry-After", "17")
            .withBody("""{"message": "Too many"}""")
        )
    )
    val client = createWorkOSClient(RetryConfig.DISABLED)
    val ex =
      assertThrows(RateLimitException::class.java) {
        client.baseClient.request(makeRequest(), Map::class.java)
      }
    assertEquals(17L, ex.retryAfterSeconds)
    assertEquals("Too many", ex.message)
  }

  @Test
  fun `429 without Retry-After leaves retryAfterSeconds null`() {
    val ex =
      run {
        wireMockRule.stubFor(
          get(urlPathEqualTo("/errors"))
            .willReturn(
              aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/json")
                .withHeader("X-Request-Id", "req_01TEST")
                .withBody("""{"message": "Too many"}""")
            )
        )
        val client = createWorkOSClient(RetryConfig.DISABLED)
        assertThrows(RateLimitException::class.java) {
          client.baseClient.request(makeRequest(), Map::class.java)
        }
      }
    assertNull(ex.retryAfterSeconds)
  }

  @Test
  fun `500 maps to GenericServerException`() {
    val ex = runErrorCase<GenericServerException>(500, """{"message": "boom"}""")
    assertEquals(500, ex.status)
  }

  @Test
  fun `502 maps to GenericServerException with 502 status`() {
    val ex = runErrorCase<GenericServerException>(502, """{"message": "bad gateway"}""")
    assertEquals(502, ex.status)
  }

  @Test
  fun `503 maps to GenericServerException with 503 status`() {
    val ex = runErrorCase<GenericServerException>(503, """{"message": "unavailable"}""")
    assertEquals(503, ex.status)
  }

  @Test
  fun `error translator tolerates non-JSON body`() {
    val ex = runErrorCase<GenericServerException>(500, "<html>gateway</html>")
    assertTrue(ex.message == null || ex.message!!.isNotBlank())
  }
}
