package com.workos.test

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.github.tomakehurst.wiremock.junit.WireMockRule
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

    fun stubResponse(url: String, responseBody: String, responseStatus: Int = 200) {
        stubFor(
            any(urlPathEqualTo(url))
                .willReturn(
                    aResponse()
                        .withStatus(responseStatus)
                        .withBody(responseBody)
                )
        )
    }
}
