// @oagen-ignore-file
package com.workos.workloadidentity.aws

import com.sun.net.httpserver.HttpServer
import com.workos.workloadidentity.IdentityTokenProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.awscore.exception.AwsErrorDetails
import software.amazon.awssdk.core.exception.ApiCallTimeoutException
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.StsClientBuilder
import software.amazon.awssdk.services.sts.endpoints.StsEndpointProvider
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenRequest
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenResponse
import software.amazon.awssdk.services.sts.model.StsException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URI
import java.time.Duration
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger

/**
 * Conformance cases WIF-AWS-01 and WIF-AWS-02, and the rules for the provider's STS client: one request per call,
 * a limit on connecting and on the whole request, and a client that can be closed. Nothing here reaches AWS: the
 * STS call is stubbed, or the provider's own client is pointed at a server on this machine.
 */
class WIFAwsStsTest {
  private val loopback = InetAddress.getLoopbackAddress()

  /**
   * A provider over the STS client that [builder] describes, pointed at [endpoint]. The builder is the provider's
   * own, so its retry rule and time limits are the ones under test; only the endpoint and the credentials are
   * supplied here.
   */
  private fun providerAt(
    endpoint: URI,
    builder: StsClientBuilder = stsClientBuilder("us-east-1")
  ): AwsStsProvider {
    val client =
      builder
        .endpointOverride(endpoint)
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test-access-key", "test-secret-key")))
        .build()
    return awsSts("https://api.workos.com", closeClient = client::close) { client.getWebIdentityToken(it) }
  }

  /** Run [block] against a local stand-in for STS that answers every request with the error [code] and counts them. */
  private fun withStsAnswering(
    status: Int,
    code: String,
    block: (endpoint: URI, requests: AtomicInteger) -> Unit
  ) {
    val requests = AtomicInteger()
    val server = HttpServer.create(InetSocketAddress(loopback, 0), 0)
    server.createContext("/") { exchange ->
      requests.incrementAndGet()
      exchange.requestBody.readAllBytes()
      val body =
        (
          """<ErrorResponse xmlns="https://sts.amazonaws.com/doc/2011-06-15/"><Error><Type>Sender</Type>""" +
            "<Code>$code</Code><Message>The request was refused.</Message></Error>" +
            "<RequestId>request-1</RequestId></ErrorResponse>"
        ).toByteArray()
      exchange.responseHeaders.add("Content-Type", "text/xml")
      exchange.sendResponseHeaders(status, body.size.toLong())
      exchange.responseBody.use { it.write(body) }
    }
    server.start()
    try {
      block(URI.create("http://127.0.0.1:${server.address.port}"), requests)
    } finally {
      server.stop(0)
    }
  }

  /**
   * Run [block] with a local port where a connection attempt gets no answer. The listener never accepts, and its
   * queue of waiting connections is filled first: once it is full, the operating system drops further attempts, as
   * a host that is down would.
   */
  private fun withUnansweredPort(block: (port: Int) -> Unit) {
    ServerSocket(0, 1, loopback).use { listener ->
      val waiting = mutableListOf<Socket>()
      try {
        var full = false
        while (!full) {
          check(waiting.size < 64) { "the listener kept completing connections that nothing accepts" }
          val socket = Socket()
          try {
            socket.connect(InetSocketAddress(loopback, listener.localPort), 500)
            waiting += socket
          } catch (_: SocketTimeoutException) {
            socket.close()
            full = true
          }
        }
        block(listener.localPort)
      } finally {
        waiting.forEach { it.close() }
      }
    }
  }

  private fun causes(error: Throwable): List<Throwable> = generateSequence(error) { it.cause }.take(16).toList()

  @Test
  fun `WIF-AWS-01 calls GetWebIdentityToken with the audience and RS256 and returns the token`() {
    val requests = mutableListOf<GetWebIdentityTokenRequest>()
    val provider =
      awsSts("https://api.workos.com") { request ->
        requests += request
        GetWebIdentityTokenResponse.builder().webIdentityToken("aws-jwt").build()
      }

    val token = provider.getToken()

    assertEquals("aws-jwt", token)
    val request = requests.single()
    assertEquals(listOf("https://api.workos.com"), request.audience())
    assertEquals("RS256", request.signingAlgorithm())
    // The lifetime is left for STS to choose.
    assertNull(request.durationSeconds())
  }

  @Test
  fun `WIF-AWS-01 the STS client is configured for the given region and not the global endpoint`() {
    stsClient("us-east-1").use { client ->
      val configuration = client.serviceClientConfiguration()
      assertEquals(Region.US_EAST_1, configuration.region())
      assertFalse(configuration.region().isGlobalRegion)
      assertFalse(configuration.endpointOverride().isPresent)
      // The endpoint the AWS SDK resolves for that configuration is the regional one.
      val endpoint =
        StsEndpointProvider
          .defaultProvider()
          .resolveEndpoint { it.region(configuration.region()) }
          .join()
      assertEquals("sts.us-east-1.amazonaws.com", endpoint.endpointUrl().host())
    }
  }

  @Test
  fun `the STS client does not retry by itself`() {
    stsClient("us-east-1").use { client ->
      val strategy = client.serviceClientConfiguration().overrideConfiguration().retryStrategy()
      assertTrue(strategy.isPresent)
      assertEquals(1, strategy.get().maxAttempts())
    }
  }

  @Test
  fun `one provider call is one STS request when STS throttles or fails`() {
    val refusals = listOf(400 to "Throttling", 429 to "ThrottlingException", 500 to "InternalFailure", 503 to "ServiceUnavailable")
    for ((status, code) in refusals) {
      withStsAnswering(status, code) { endpoint, requests ->
        providerAt(endpoint).use { provider ->
          val error = assertThrows(IllegalStateException::class.java) { provider.getToken() }

          assertEquals("sts:GetWebIdentityToken failed with $code (HTTP $status).", error.message)
          assertEquals(1, requests.get(), "STS requests made by one provider call that got HTTP $status $code")
        }
      }
    }
  }

  @Test
  fun `the STS client gives the whole request ten seconds`() {
    stsClient("us-east-1").use { client ->
      val configuration = client.serviceClientConfiguration().overrideConfiguration()
      assertEquals(Optional.of(Duration.ofSeconds(10)), configuration.apiCallTimeout())
    }
  }

  @Test
  fun `an STS that accepts the connection and never answers fails the call at the request limit`() {
    ServerSocket(0, 50, loopback).use { silent ->
      val builder = stsClientBuilder("us-east-1", requestTimeout = Duration.ofMillis(300))
      providerAt(URI.create("http://127.0.0.1:${silent.localPort}"), builder).use { provider ->
        val started = System.nanoTime()
        val error = assertThrows(IllegalStateException::class.java) { provider.getToken() }
        val elapsed = Duration.ofNanos(System.nanoTime() - started)

        assertEquals("sts:GetWebIdentityToken failed: ApiCallTimeoutException.", error.message)
        assertInstanceOf(ApiCallTimeoutException::class.java, error.cause)
        // With no request limit the call ends only when the HTTP client stops waiting to read, after 30 seconds.
        assertTrue(elapsed < Duration.ofSeconds(10), "the call took $elapsed")
      }
    }
  }

  @Test
  fun `an STS that cannot be connected to fails the call at the connect limit`() {
    withUnansweredPort { port ->
      // Longer than the two seconds the AWS SDK allows by itself, and shorter than the request limit, so only the
      // provider's connect limit can end the call at this time.
      val limit = Duration.ofSeconds(3)
      providerAt(URI.create("http://127.0.0.1:$port"), stsClientBuilder("us-east-1", connectTimeout = limit)).use { provider ->
        val started = System.nanoTime()
        val error = assertThrows(IllegalStateException::class.java) { provider.getToken() }
        val elapsed = Duration.ofNanos(System.nanoTime() - started)

        assertTrue(
          causes(error).any { it.message.orEmpty().contains("connect timed out", ignoreCase = true) },
          "not a connect timeout: ${causes(error)}"
        )
        assertTrue(elapsed >= limit.minusMillis(300), "the call ended after $elapsed, before the connect limit")
        assertTrue(elapsed < Duration.ofSeconds(9), "the call ended after $elapsed, at the request limit")
      }
    }
  }

  @Test
  fun `closing the provider closes its STS client once and a closed provider makes no STS call`() {
    val closes = AtomicInteger()
    val calls = AtomicInteger()
    val provider =
      awsSts("https://api.workos.com", closeClient = { closes.incrementAndGet() }) {
        calls.incrementAndGet()
        GetWebIdentityTokenResponse.builder().webIdentityToken("aws-jwt").build()
      }
    assertEquals("aws-jwt", provider.getToken())

    provider.close()
    provider.close()

    assertEquals(1, closes.get())
    val error = assertThrows(IllegalStateException::class.java) { provider.getToken() }
    assertTrue(error.message.orEmpty().contains("closed"), "unexpected message: ${error.message}")
    assertEquals(1, calls.get())
  }

  @Test
  fun `the provider built for a region can be closed`() {
    val provider: AutoCloseable = awsSts("https://api.workos.com", region = "us-east-1")

    provider.close()

    val error = assertThrows(IllegalStateException::class.java) { (provider as IdentityTokenProvider).getToken() }
    assertTrue(error.message.orEmpty().contains("closed"), "unexpected message: ${error.message}")
  }

  @Test
  fun `WIF-AWS-01 the region the AWS SDK resolves is used when none is passed`() {
    stsClient(null) { Region.EU_WEST_1 }.use { client ->
      assertEquals(Region.EU_WEST_1, client.serviceClientConfiguration().region())
    }
  }

  @Test
  fun `WIF-AWS-01 building the provider with no region available is an error`() {
    val missing = assertThrows(IllegalStateException::class.java) { stsClient(null) { null } }
    assertTrue(missing.message.orEmpty().contains("region"))

    assertThrows(IllegalStateException::class.java) { stsClient("aws-global") }
    assertThrows(IllegalStateException::class.java) { stsClient(" ") { null } }
  }

  @Test
  fun `WIF-AWS-02 an STS error surfaces as a provider error that names the call`() {
    val denied =
      StsException
        .builder()
        .statusCode(403)
        .awsErrorDetails(
          AwsErrorDetails
            .builder()
            .errorCode("AccessDenied")
            .errorMessage("User is not authorized to perform: sts:GetWebIdentityToken")
            .build()
        ).message("User is not authorized to perform: sts:GetWebIdentityToken")
        .build()
    val provider = awsSts("https://api.workos.com") { throw denied }

    val error = assertThrows(IllegalStateException::class.java) { provider.getToken() }

    val message = error.message.orEmpty()
    assertTrue(message.contains("sts:GetWebIdentityToken"), "unexpected message: $message")
    assertTrue(message.contains("AccessDenied"), "unexpected message: $message")
    assertFalse(message.contains("aws-jwt"))
    assertEquals(denied, error.cause)
  }

  @Test
  fun `WIF-AWS-02 a client-side AWS failure and an empty token are provider errors that name the call`() {
    val unreachable = awsSts("https://api.workos.com") { throw SdkClientException.create("Unable to load credentials") }
    val clientError = assertThrows(IllegalStateException::class.java) { unreachable.getToken() }
    assertTrue(clientError.message.orEmpty().contains("sts:GetWebIdentityToken"))

    val empty = awsSts("https://api.workos.com") { GetWebIdentityTokenResponse.builder().build() }
    val noToken = assertThrows(IllegalStateException::class.java) { empty.getToken() }
    assertTrue(noToken.message.orEmpty().contains("sts:GetWebIdentityToken"))
  }

  @Test
  fun `an empty audience is rejected when the provider is built`() {
    assertThrows(IllegalArgumentException::class.java) { awsSts(" ") { GetWebIdentityTokenResponse.builder().build() } }
    assertThrows(IllegalArgumentException::class.java) { awsSts(" ", region = "us-east-1") }
  }
}
