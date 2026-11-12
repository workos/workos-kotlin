// @oagen-ignore-file
@file:JvmName("AwsSts")

package com.workos.workloadidentity.aws

import com.workos.workloadidentity.IdentityTokenProvider
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
import software.amazon.awssdk.retries.DefaultRetryStrategy
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenRequest
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenResponse

private const val SIGNING_ALGORITHM = "RS256"

/**
 * An [IdentityTokenProvider] for AWS. It calls STS `GetWebIdentityToken`
 * with the workload's own AWS credentials, found through the AWS SDK's
 * default credential chain, and returns the token issued for [audience].
 *
 * The workload's role needs permission for `sts:GetWebIdentityToken`, and the
 * account needs outbound web identity federation enabled. The conventional
 * audience for WorkOS is `https://api.workos.com`.
 *
 * Kotlin usage:
 * ```kotlin
 * val provider = awsSts("https://api.workos.com", region = "us-east-1")
 * ```
 *
 * Java usage:
 * ```java
 * IdentityTokenProvider provider = AwsSts.awsSts("https://api.workos.com", "us-east-1");
 * ```
 *
 * The STS client is built once, here, and always uses a regional endpoint.
 * The provider does not cache and does not retry: the AWS SDK's own retries
 * are switched off for this client, because the WorkOS SDK calls the provider
 * again when an exchange attempt fails.
 *
 * @param audience the audience the token is issued for.
 * @param region the AWS region whose STS endpoint to call. When left out, the AWS SDK's
 *   own region lookup is used (`AWS_REGION`, the shared config file, instance metadata).
 * @throws IllegalStateException when no region is given and the AWS SDK cannot find one,
 *   or when the region is the global one.
 */
@JvmOverloads
fun awsSts(
  audience: String,
  region: String? = null
): IdentityTokenProvider {
  val client = stsClient(region)
  return awsSts(audience) { client.getWebIdentityToken(it) }
}

/** The one STS call the provider makes. Tests supply a stub; nothing else in this module touches AWS. */
internal fun interface WebIdentityTokenCall {
  fun getWebIdentityToken(request: GetWebIdentityTokenRequest): GetWebIdentityTokenResponse
}

/** [awsSts] over a given STS call, for tests. Hidden from Java. */
@JvmSynthetic
internal fun awsSts(
  audience: String,
  sts: WebIdentityTokenCall
): IdentityTokenProvider {
  require(audience.isNotBlank()) { "audience is required" }
  // DurationSeconds is left unset, so STS applies its default lifetime.
  val request =
    GetWebIdentityTokenRequest
      .builder()
      .audience(audience)
      .signingAlgorithm(SIGNING_ALGORITHM)
      .build()
  return IdentityTokenProvider {
    val token =
      try {
        sts.getWebIdentityToken(request).webIdentityToken()
      } catch (e: AwsServiceException) {
        val code = e.awsErrorDetails()?.errorCode() ?: "an unknown error"
        throw IllegalStateException("sts:GetWebIdentityToken failed with $code (HTTP ${e.statusCode()}).", e)
      } catch (e: SdkException) {
        throw IllegalStateException("sts:GetWebIdentityToken failed: ${e.javaClass.simpleName}.", e)
      }
    check(!token.isNullOrEmpty()) { "sts:GetWebIdentityToken returned no token." }
    token
  }
}

/** Build the STS client for [region], or for the region the AWS SDK resolves when [region] is not given. */
@JvmSynthetic
internal fun stsClient(
  region: String?,
  resolveRegion: () -> Region? = ::defaultRegion
): StsClient {
  val resolved =
    region?.takeIf { it.isNotBlank() }?.let { Region.of(it) }
      ?: resolveRegion()
      ?: error("awsSts needs an AWS region. Pass one, or set AWS_REGION.")
  // GetWebIdentityToken is not served by the global endpoint.
  check(!resolved.isGlobalRegion) { "awsSts needs a regional STS endpoint, and ${resolved.id()} is not a region." }
  return StsClient
    .builder()
    .region(resolved)
    // Providers do not retry. The exchange retries the whole provider call with the SDK's own backoff.
    .overrideConfiguration { it.retryStrategy(DefaultRetryStrategy.doNotRetry()) }
    .build()
}

private fun defaultRegion(): Region? =
  try {
    DefaultAwsRegionProviderChain().region
  } catch (_: SdkClientException) {
    null
  }
