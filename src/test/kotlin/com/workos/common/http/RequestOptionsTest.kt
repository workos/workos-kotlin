// @oagen-ignore-file
package com.workos.common.http

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.workos.WorkOS
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.concurrent.TimeUnit

class RequestOptionsTest {
  data class Thing(
    val id: String
  )

  @Test
  fun `additional headers reach the wire`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/things"))
        .withHeader("X-Trace-Id", equalTo("abc-123"))
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"t_1\"}"))
    )
    val workos = buildClient()
    val options = RequestOptions.builder().header("X-Trace-Id", "abc-123").build()
    val result =
      workos.baseClient.request(
        RequestConfig("POST", "/things", body = mapOf("name" to "thing"), requestOptions = options),
        Thing::class.java
      )
    assertEquals("t_1", result.id)
  }

  @Test
  fun `idempotencyKey override is honored`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/things"))
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"t_2\"}"))
    )
    val workos = buildClient()
    val options = RequestOptions.builder().idempotencyKey("my-key").build()
    workos.baseClient.request(
      RequestConfig("POST", "/things", body = mapOf("name" to "thing"), requestOptions = options),
      Thing::class.java
    )
    wireMockRule.verify(
      postRequestedFor(urlPathEqualTo("/things")).withHeader("Idempotency-Key", equalTo("my-key"))
    )
  }

  @Test
  fun `per-request apiKey override goes into Authorization header`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/things"))
        .withHeader("Authorization", equalTo("Bearer sk_override"))
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"t_3\"}"))
    )
    val workos = buildClient()
    val options = RequestOptions.builder().apiKey("sk_override").build()
    val result =
      workos.baseClient.request(
        RequestConfig("POST", "/things", body = emptyMap<String, Any>(), requestOptions = options),
        Thing::class.java
      )
    assertEquals("t_3", result.id)
  }

  @Test
  fun `baseUrl override redirects the request`() {
    // Stub on a different path — if baseUrl override works, WireMock is still the same host but the host comes from options
    wireMockRule.stubFor(
      post(urlPathEqualTo("/things"))
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"t_4\"}"))
    )
    val workos = buildClient(apiBaseUrl = "http://localhost:1/not-real")
    val options = RequestOptions.builder().baseUrl(wireMockRule.baseUrl()).build()
    val result =
      workos.baseClient.request(
        RequestConfig("POST", "/things", body = emptyMap<String, Any>(), requestOptions = options),
        Thing::class.java
      )
    assertEquals("t_4", result.id)
  }

  @Test
  fun `auto-generated idempotency key is added when retries are enabled`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/things"))
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"t_5\"}"))
    )
    val workos = buildClient(retryConfig = RetryConfig(maxRetries = 2, baseDelayMs = 1, maxDelayMs = 1, jitter = 0.0))
    workos.baseClient.request(
      RequestConfig("POST", "/things", body = emptyMap<String, Any>()),
      Thing::class.java
    )
    wireMockRule.verify(
      postRequestedFor(urlPathEqualTo("/things"))
        .withHeader("Idempotency-Key", WireMock.matching(".+"))
    )
  }

  @Test
  fun `auto-generated idempotency key is not added when retries are disabled`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/things"))
        .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"t_6\"}"))
    )
    val workos = buildClient(retryConfig = RetryConfig.DISABLED)
    workos.baseClient.request(
      RequestConfig("POST", "/things", body = emptyMap<String, Any>()),
      Thing::class.java
    )
    wireMockRule.verify(
      postRequestedFor(urlPathEqualTo("/things")).withoutHeader("Idempotency-Key")
    )
  }

  private fun buildClient(
    apiBaseUrl: String = wireMockRule.baseUrl(),
    retryConfig: RetryConfig = RetryConfig.DISABLED
  ): WorkOS =
    WorkOS(
      apiKey = "sk_test_example",
      apiBaseUrl = apiBaseUrl,
      httpClient = OkHttpClient.Builder().callTimeout(5, TimeUnit.SECONDS).build(),
      retryConfig = retryConfig
    )

  companion object {
    @RegisterExtension
    @JvmField
    val wireMockRule: WireMockExtension =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort())
        .build()
  }
}
