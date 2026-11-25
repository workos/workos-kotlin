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
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NoRouteToHostException
import java.net.Proxy
import java.net.ProxySelector
import java.net.ServerSocket
import java.net.SocketAddress
import java.net.SocketException
import java.net.URI
import java.net.UnknownHostException
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpTimeoutException
import java.nio.channels.ClosedChannelException
import java.nio.channels.UnresolvedAddressException
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

/**
 * Conformance cases WIF-GCP-01 to WIF-GCP-03, against a local mock of the
 * metadata server, and the error for a metadata server that cannot be reached.
 */
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
  fun `WIF-GCP-03 the request never goes through a proxy`() {
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

  @Test
  fun `a metadata host name that does not resolve is an error that names the host`() {
    // The top-level domain `invalid` is reserved and never resolves.
    val provider = gcpMetadata("https://api.workos.com", "http://metadata.invalid")

    val error = assertThrows(IOException::class.java) { provider.getToken() }

    assertEquals(
      "The GCP metadata server could not be reached: metadata.invalid could not be resolved. " +
        "This provider works only on Google Cloud (Compute Engine, GKE, Cloud Run, Cloud Functions).",
      error.message
    )
    assertTrue(causes(error).any { it is UnresolvedAddressException })
  }

  @Test
  fun `a refused connection to the metadata server is an error that says so`() {
    // Nothing listens on a port that was just closed.
    val port = ServerSocket(0, 1, InetAddress.getLoopbackAddress()).use { it.localPort }
    val provider = gcpMetadata("https://api.workos.com", "http://127.0.0.1:$port")

    val error = assertThrows(IOException::class.java) { provider.getToken() }

    assertEquals(
      "The GCP metadata server could not be reached: connection refused. " +
        "This provider works only on Google Cloud (Compute Engine, GKE, Cloud Run, Cloud Functions).",
      error.message
    )
    assertTrue(error.cause is ConnectException)
  }

  @Test
  fun `a metadata server that does not answer in time is an error that gives the timeout`() {
    // The operating system completes the connection, and nothing ever accepts it or answers.
    ServerSocket(0, 1, InetAddress.getLoopbackAddress()).use { silent ->
      val provider = gcpMetadata("https://api.workos.com", "http://127.0.0.1:${silent.localPort}", Duration.ofMillis(250))

      val error = assertThrows(IOException::class.java) { provider.getToken() }

      assertEquals(
        "The GCP metadata server could not be reached: timed out after 0.25 seconds. " +
          "This provider works only on Google Cloud (Compute Engine, GKE, Cloud Run, Cloud Functions).",
        error.message
      )
      assertTrue(error.cause is HttpTimeoutException)
    }
  }

  @Test
  fun `the reason is read from the whole cause chain and is a class name only as a last resort`() {
    fun reason(failure: IOException): String {
      val error = unreachable(failure, "metadata.google.internal", Duration.ofSeconds(10))
      assertSame(failure, error.cause)
      return error.message
        .orEmpty()
        .removePrefix("The GCP metadata server could not be reached: ")
        .removeSuffix(". This provider works only on Google Cloud (Compute Engine, GKE, Cloud Run, Cloud Functions).")
    }

    fun <T : Throwable> T.causedBy(cause: Throwable): T = apply { initCause(cause) }

    // What java.net.http throws for a name that does not resolve: no message anywhere.
    assertEquals(
      "metadata.google.internal could not be resolved",
      reason(ConnectException().causedBy(ConnectException().causedBy(UnresolvedAddressException())))
    )
    assertEquals("metadata.google.internal could not be resolved", reason(UnknownHostException("metadata.google.internal")))
    assertEquals("timed out after 10 seconds", reason(HttpConnectTimeoutException("HTTP connect timed out")))
    assertEquals("timed out after 10 seconds", reason(HttpTimeoutException("request timed out")))
    // A refused connection, with the message and without one.
    assertEquals("connection refused", reason(ConnectException("Connection refused")))
    assertEquals("connection refused", reason(ConnectException().causedBy(ClosedChannelException())))
    // Anything else: the first message in the chain.
    assertEquals("No route to host", reason(ConnectException("No route to host").causedBy(NoRouteToHostException("No route to host"))))
    assertEquals("Connection reset", reason(IOException(SocketException("Connection reset."))))
    // No message anywhere: the class of the root cause.
    assertEquals("ClosedChannelException", reason(IOException(ClosedChannelException())))
  }

  private fun causes(error: Throwable): List<Throwable> = generateSequence(error.cause) { it.cause }.toList()

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
