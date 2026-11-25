// @oagen-ignore-file
@file:JvmName("AwsSts")

package com.workos.workloadidentity.aws

import com.workos.workloadidentity.IdentityTokenProvider
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.http.apache5.Apache5HttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
import software.amazon.awssdk.retries.DefaultRetryStrategy
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.StsClientBuilder
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenRequest
import software.amazon.awssdk.services.sts.model.GetWebIdentityTokenResponse
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

private const val SIGNING_ALGORITHM = "RS256"

/** The limit on connecting to STS, and the limit on one whole STS request. The other providers use the same value. */
private val TIMEOUT: Duration = Duration.ofSeconds(10)

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
 * AwsStsProvider provider = AwsSts.awsSts("https://api.workos.com", "us-east-1");
 * ```
 *
 * The STS client is built once, here, and always uses a regional endpoint.
 * The provider does not cache and does not retry: the AWS SDK's own retries
 * are switched off for this client, because the WorkOS SDK calls the provider
 * again when an exchange attempt fails. One call is one STS request, with 10
 * seconds to connect and 10 seconds for the whole request.
 *
 * The provider owns its STS client. Keep one provider for as long as the
 * credentials that use it, and [close][AwsStsProvider.close] a provider that
 * is no longer used.
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
): AwsStsProvider {
  // Checked before the client is built, so a refused audience leaves no client behind.
  require(audience.isNotBlank()) { "audience is required" }
  val client = stsClient(region)
  return AwsStsProvider(audience, { client.getWebIdentityToken(it) }, client::close)
}

/**
 * The [IdentityTokenProvider] that [awsSts] returns.
 *
 * It owns an STS client, which holds an HTTP connection pool. A provider that
 * is used for the life of the application needs no care. Call [close] on a
 * provider that is replaced or no longer used, to release the pool at once
 * and not when the provider is garbage collected.
 */
class AwsStsProvider internal constructor(
  audience: String,
  private val sts: WebIdentityTokenCall,
  private val closeClient: () -> Unit
) : IdentityTokenProvider,
  AutoCloseable {
  init {
    require(audience.isNotBlank()) { "audience is required" }
  }

  // DurationSeconds is left unset, so STS applies its default lifetime.
  private val request: GetWebIdentityTokenRequest =
    GetWebIdentityTokenRequest
      .builder()
      .audience(audience)
      .signingAlgorithm(SIGNING_ALGORITHM)
      .build()

  private val closed = AtomicBoolean(false)

  /**
   * Ask STS for a token. Makes one STS request.
   *
   * @throws IllegalStateException when STS refuses or cannot be reached, with a message that
   *   names `sts:GetWebIdentityToken`, or when the provider is closed.
   */
  override fun getToken(): String {
    check(!closed.get()) { "This awsSts provider is closed. Build a new one with awsSts." }
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
    return token
  }

  /** Close the STS client and its connection pool. A closed provider cannot be used again. Closing twice is harmless. */
  override fun close() {
    if (closed.compareAndSet(false, true)) closeClient()
  }
}

/** The one STS call the provider makes. Tests supply a stub; nothing else in this module touches AWS. */
internal fun interface WebIdentityTokenCall {
  fun getWebIdentityToken(request: GetWebIdentityTokenRequest): GetWebIdentityTokenResponse
}

/** [awsSts] over a given STS call, for tests. Hidden from Java. */
@JvmSynthetic
internal fun awsSts(
  audience: String,
  closeClient: () -> Unit = {},
  sts: WebIdentityTokenCall
): AwsStsProvider = AwsStsProvider(audience, sts, closeClient)

/**
 * The builder of the STS client for [region], or for the region the AWS SDK
 * resolves when [region] is not given. Everything the provider decides about
 * its client is set here: the region, no retries, and the two time limits.
 * Tests add an endpoint and credentials of their own before they build.
 */
@JvmSynthetic
internal fun stsClientBuilder(
  region: String?,
  resolveRegion: () -> Region? = ::defaultRegion,
  connectTimeout: Duration = TIMEOUT,
  requestTimeout: Duration = TIMEOUT
): StsClientBuilder {
  val resolved =
    region?.takeIf { it.isNotBlank() }?.let { Region.of(it) }
      ?: resolveRegion()
      ?: error("awsSts needs an AWS region. Pass one, or set AWS_REGION.")
  // GetWebIdentityToken is not served by the global endpoint.
  check(!resolved.isGlobalRegion) { "awsSts needs a regional STS endpoint, and ${resolved.id()} is not a region." }
  return StsClient
    .builder()
    .region(resolved)
    // The limit on connecting belongs to the HTTP client. The STS client builds it from this builder and closes
    // it when the STS client is closed.
    .httpClientBuilder(Apache5HttpClient.builder().connectionTimeout(connectTimeout))
    .overrideConfiguration {
      // Providers do not retry. The exchange retries the whole provider call with the SDK's own backoff.
      it.retryStrategy(DefaultRetryStrategy.doNotRetry())
      // With one attempt, the limit on the API call is the limit on the whole request.
      it.apiCallTimeout(requestTimeout)
    }
}

/** Build the STS client for [region], or for the region the AWS SDK resolves when [region] is not given. */
@JvmSynthetic
internal fun stsClient(
  region: String?,
  resolveRegion: () -> Region? = ::defaultRegion
): StsClient = stsClientBuilder(region, resolveRegion).build()

private fun defaultRegion(): Region? =
  try {
    DefaultAwsRegionProviderChain().region
  } catch (_: SdkClientException) {
    null
  }
