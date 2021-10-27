package com.workos.test

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.workos.WorkOS
import org.junit.ClassRule

open class TestBase {
    companion object {
        @ClassRule
        @JvmField
        val wireMockRule = WireMockRule(8081)
    }

    fun createWorkOSClient(): WorkOS {
        val workos = WorkOS("apiKey")
        workos.port = 8081
        workos.apiHostname = "localhost"
        workos.https = false
        return workos
    }

    fun stubResponse(url: String, responseBody: String, responseStatus: Int = 200): StubMapping {
        return stubFor(
            any(urlPathEqualTo(url))
                .willReturn(
                    aResponse()
                        .withStatus(responseStatus)
                        .withBody(responseBody)
                        .withHeader("X-Request-ID", "request_id_value")
                )
        )
    }

    fun deleteStub(stub: StubMapping) {
        removeStub(stub)
    }
}
