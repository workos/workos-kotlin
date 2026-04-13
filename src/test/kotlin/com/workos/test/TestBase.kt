// @oagen-ignore-file
package com.workos.test

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.workos.WorkOS
import com.workos.common.http.RetryConfig
import okhttp3.OkHttpClient
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.concurrent.TimeUnit

/** Shared WireMock harness for every generated service test. */
open class TestBase {
  protected fun createWorkOSClient(retryConfig: RetryConfig = RetryConfig.DISABLED): WorkOS {
    val http =
      OkHttpClient
        .Builder()
        .callTimeout(5, TimeUnit.SECONDS)
        .build()
    return WorkOS(
      apiKey = "sk_test_example",
      clientId = "client_01TEST",
      apiBaseUrl = wireMockRule.baseUrl(),
      httpClient = http,
      retryConfig = retryConfig
    )
  }

  protected fun stubResponse(
    method: String,
    path: String,
    status: Int = 200,
    body: String = "{}",
    headers: Map<String, String> = emptyMap()
  ) {
    val mappingBuilder =
      when (method.uppercase()) {
        "GET" -> WireMock.get(WireMock.urlPathMatching(escapePath(path)))
        "POST" -> WireMock.post(WireMock.urlPathMatching(escapePath(path)))
        "PUT" -> WireMock.put(WireMock.urlPathMatching(escapePath(path)))
        "PATCH" -> WireMock.patch(WireMock.urlPathMatching(escapePath(path)))
        "DELETE" -> WireMock.delete(WireMock.urlPathMatching(escapePath(path)))
        else -> WireMock.any(WireMock.urlPathMatching(escapePath(path)))
      }
    val response =
      WireMock
        .aResponse()
        .withStatus(status)
        .withHeader("Content-Type", "application/json")
        .withBody(body)
    headers.forEach { (k, v) -> response.withHeader(k, v) }
    wireMockRule.stubFor(mappingBuilder.willReturn(response))
  }

  /** Escape path parameters (e.g. `{id}`) as regex wildcards for WireMock. */
  private fun escapePath(path: String): String = path.replace(Regex("\\{[^}]+}"), "[^/]+")

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
