// @oagen-ignore-file
package com.workos.common.http

import com.fasterxml.jackson.core.type.TypeReference
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.workos.WorkOS
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PageAutoPagingTest {
  data class Item(
    val id: String
  )

  @Test
  fun `autoPagingIterable walks the cursor chain`() {
    // Page 1 → after = cursor-2
    wireMockRule.stubFor(
      get(urlPathEqualTo("/things"))
        .withQueryParam("limit", WireMock.equalTo("2"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                "data": [
                  {"id": "a"},
                  {"id": "b"}
                ],
                "list_metadata": {"before": null, "after": "cursor-2"}
              }
              """.trimIndent()
            )
        )
    )
    // Page 2 → after = cursor-3
    wireMockRule.stubFor(
      get(urlPathEqualTo("/things"))
        .withQueryParam("after", WireMock.equalTo("cursor-2"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                "data": [
                  {"id": "c"},
                  {"id": "d"}
                ],
                "list_metadata": {"before": null, "after": "cursor-3"}
              }
              """.trimIndent()
            )
        )
    )
    // Page 3 → terminal (after = null)
    wireMockRule.stubFor(
      get(urlPathEqualTo("/things"))
        .withQueryParam("after", WireMock.equalTo("cursor-3"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                "data": [{"id": "e"}],
                "list_metadata": {"before": null, "after": null}
              }
              """.trimIndent()
            )
        )
    )

    val workos = buildClient()
    val itemType = object : TypeReference<Item>() {}

    fun configFor(after: String?): RequestConfig {
      val params = mutableListOf<Pair<String, String>>()
      params += "limit" to "2"
      if (after != null) params += "after" to after
      return RequestConfig(method = "GET", path = "/things", queryParams = params)
    }

    val first = workos.baseClient.requestPage(configFor(null), itemType) { after -> configFor(after) }
    assertEquals(listOf("a", "b"), first.data.map { it.id })
    assertTrue(first.hasNextPage())

    val allIds = first.autoPagingIterable().map { it.id }.toList()
    assertEquals(listOf("a", "b", "c", "d", "e"), allIds)
  }

  @Test
  fun `empty page reports no next`() {
    wireMockRule.stubFor(
      get(urlPathEqualTo("/things"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"data": [], "list_metadata": {"before": null, "after": null}}"""
            )
        )
    )

    val workos = buildClient()
    val itemType = object : TypeReference<Item>() {}
    val config = RequestConfig(method = "GET", path = "/things")
    val page = workos.baseClient.requestPage(config, itemType) { _ -> config }
    assertTrue(page.data.isEmpty())
    assertFalse(page.hasNextPage())
    assertTrue(page.autoPagingIterable().toList().isEmpty())
  }

  private fun buildClient(): WorkOS =
    WorkOS(
      apiKey = "sk_test_example",
      apiBaseUrl = wireMockRule.baseUrl(),
      httpClient = OkHttpClient.Builder().build(),
      retryConfig = RetryConfig.DISABLED
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
