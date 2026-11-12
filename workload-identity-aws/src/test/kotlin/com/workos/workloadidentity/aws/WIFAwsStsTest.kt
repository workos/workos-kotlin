// @oagen-ignore-file
package com.workos.workloadidentity.aws

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import software.amazon.awssdk.awscore.exception.AwsErrorDetails
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.endpoints.StsEndpointProvider
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenRequest
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenResponse
import software.amazon.awssdk.services.sts.model.StsException

/** Conformance cases WIF-AWS-01 and WIF-AWS-02. The STS call is stubbed; nothing here reaches AWS. */
class WIFAwsStsTest {
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
  }
}
