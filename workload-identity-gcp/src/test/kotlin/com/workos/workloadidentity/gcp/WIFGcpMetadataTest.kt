// @oagen-ignore-file
package com.workos.workloadidentity.gcp

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger

/** Conformance cases WIF-GCP-01 and WIF-GCP-02, against a local mock of the metadata server. */
class WIFGcpMetadataTest {
  private fun provider(audience: String = "https://api.workos.com") = gcpMetadata(audience, metadata.baseUrl())

  @Test
  fun `WIF-GCP-01 gets the metadata identity URL with the encoded audience and format=full and the metadata header`() {
    metadata.stubFor(get(urlPathEqualTo(IDENTITY_PATH)).willReturn(aResponse().withStatus(200).withBody("gcp-jwt\n")))

    val token = provider().getToken()

    assertEquals("gcp-jwt", token)
    val request = metadata.findAll(anyRequestedFor(anyUrl())).single()
    assertEquals("GET", request.method.value())
    assertEquals("$IDENTITY_PATH?audience=https%3A%2F%2Fapi.workos.com&format=full", request.url)
    assertEquals("Google", request.getHeader("Metadata-Flavor"))
  }

  @Test
  fun `a space in the audience is percent-encoded and not written as a plus sign`() {
    metadata.stubFor(get(urlPathEqualTo(IDENTITY_PATH)).willReturn(aResponse().withStatus(200).withBody("gcp-jwt")))

    provider("my audience+1").getToken()

    assertEquals(
      "$IDENTITY_PATH?audience=my%20audience%2B1&format=full",
      metadata.findAll(anyRequestedFor(anyUrl())).single().url
    )
  }

  @Test
  fun `WIF-GCP-02 a 404 is an error that names the status`() {
    metadata.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(404).withBody("not found")))

    val error = assertThrows(IOException::class.java) { provider().getToken() }

    assertTrue(error.message.orEmpty().contains("404"))
  }

  @Test
  fun `WIF-GCP-02 an empty body is an error`() {
    metadata.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(200).withBody(" \n")))

    assertThrows(IOException::class.java) { provider().getToken() }
  }

  @Test
  fun `WIF-GCP-02 a redirect is an error and is not followed`() {
    metadata.stubFor(
      get(urlPathEqualTo(IDENTITY_PATH)).willReturn(aResponse().withStatus(302).withHeader("Location", "${elsewhere.baseUrl()}/token"))
    )
    elsewhere.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(200).withBody("redirected-jwt")))

    assertThrows(IOException::class.java) { provider().getToken() }

    assertEquals(1, metadata.findAll(anyRequestedFor(anyUrl())).size)
    assertEquals(0, elsewhere.findAll(anyRequestedFor(anyUrl())).size)
  }

  @Test
  fun `the request never goes through a proxy`() {
    metadata.stubFor(get(urlPathEqualTo(IDENTITY_PATH)).willReturn(aResponse().withStatus(200).withBody("gcp-jwt")))
    // The JVM's default proxy selector is what -Dhttp.proxyHost and the system
    // proxy settings turn into. This one sends every request to a server that
    // must never be contacted.
    val asked = AtomicInteger()
    val original = ProxySelector.getDefault()
    ProxySelector.setDefault(
      object : ProxySelector() {
        override fun select(uri: URI): List<Proxy> {
          asked.incrementAndGet()
          return listOf(Proxy(Proxy.Type.HTTP, InetSocketAddress("localhost", elsewhere.port)))
        }

        override fun connectFailed(
          uri: URI,
          address: SocketAddress,
          failure: IOException
        ) = Unit
      }
    )

    val token =
      try {
        provider().getToken()
      } finally {
        ProxySelector.setDefault(original)
      }

    assertEquals("gcp-jwt", token)
    assertEquals(0, asked.get())
    assertEquals(1, metadata.findAll(anyRequestedFor(anyUrl())).size)
    assertEquals(0, elsewhere.findAll(anyRequestedFor(anyUrl())).size)
  }

  @Test
  fun `an empty audience is rejected when the provider is built`() {
    assertThrows(IllegalArgumentException::class.java) { gcpMetadata(" ") }
  }

  companion object {
    private const val IDENTITY_PATH = "/computeMetadata/v1/instance/service-accounts/default/identity"

    /** Stands in for the metadata server. */
    @RegisterExtension
    @JvmField
    val metadata: WireMockExtension =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort())
        .build()

    /** A second server that must never be contacted: the target of the redirect. */
    @RegisterExtension
    @JvmField
    val elsewhere: WireMockExtension =
      WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort())
        .build()
  }
}
