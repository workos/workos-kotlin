@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.workos.test

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.workos.WorkOS
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.AfterTest

open class TestBase {
  val stubs = mutableListOf<StubMapping>()

  companion object {
    @RegisterExtension
    @JvmField
    val wireMockRule =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().dynamicHttpsPort())
        .configureStaticDsl(true)
        .build()
  }

  @AfterTest
  fun afterEach() {
    for (stub in stubs) {
      deleteStub(stub)
    }

    stubs.clear()
  }

  fun createWorkOSClient(): WorkOS {
    val workos = WorkOS("apiKey")
    workos.port = getWireMockPort()
    workos.apiHostname = "localhost"
    workos.https = false
    return workos
  }

  fun getWireMockPort(): Int = wireMockRule.port

  fun stubResponse(
    url: String,
    responseBody: String,
    responseStatus: Int = 200,
    params: Map<String, StringValuePattern> = emptyMap(),
    requestBody: String? = null,
    requestHeaders: Map<String, String>? = null
  ): StubMapping {
    val mapping =
      any(urlPathEqualTo(url))
        .withQueryParams(params)
        .willReturn(
          aResponse()
            .withStatus(responseStatus)
            .withBody(responseBody)
            .withHeader("X-Request-ID", "request_id_value")
        )

    if (requestBody != null) {
      mapping.withRequestBody(equalToJson(requestBody))
    }

    if (requestHeaders != null) {
      for (header in requestHeaders.entries.iterator()) {
        mapping.withHeader(header.key, equalTo(header.value))
      }
    }

    val stub = stubFor(mapping)

    stubs.add(stub)

    return stub
  }

  fun stubResponseWithScenario(
    url: String,
    responseBody: String,
    responseStatus: Int = 200,
    params: Map<String, StringValuePattern> = emptyMap(),
    requestBody: String? = null,
    requestHeaders: Map<String, String>? = null,
    scenarioName: String,
    scenarioState: String,
    nextScenarioState: String
  ): StubMapping {
    val mapping =
      any(urlPathEqualTo(url))
        .inScenario(scenarioName)
        .whenScenarioStateIs(scenarioState)
        .withQueryParams(params)
        .willReturn(
          aResponse()
            .withStatus(responseStatus)
            .withBody(responseBody)
            .withHeader("X-Request-ID", "request_id_value")
        ).willSetStateTo(nextScenarioState)

    if (requestBody != null) {
      mapping.withRequestBody(equalToJson(requestBody))
    }

    if (requestHeaders != null) {
      for (header in requestHeaders.entries.iterator()) {
        mapping.withHeader(header.key, equalTo(header.value))
      }
    }

    val stub = stubFor(mapping)

    stubs.add(stub)

    return stub
  }

  private fun deleteStub(stub: StubMapping) {
    removeStub(stub)
  }
}
