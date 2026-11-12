// @oagen-ignore-file
@file:JvmName("GcpMetadata")

package com.workos.workloadidentity.gcp

import com.workos.workloadidentity.IdentityTokenProvider
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

private const val METADATA_BASE_URL = "http://metadata.google.internal"

private const val IDENTITY_PATH = "/computeMetadata/v1/instance/service-accounts/default/identity"

private val TIMEOUT: Duration = Duration.ofSeconds(10)

/**
 * An [IdentityTokenProvider] for Google Cloud. It asks the metadata server
 * for an identity token for the workload's default service account, issued
 * for [audience].
 *
 * It works wherever the metadata server is reachable: Compute Engine, GKE,
 * Cloud Run, Cloud Functions and App Engine. The conventional audience for
 * WorkOS is `https://api.workos.com`.
 *
 * Kotlin usage:
 * ```kotlin
 * val provider = gcpMetadata("https://api.workos.com")
 * ```
 *
 * Java usage:
 * ```java
 * IdentityTokenProvider provider = GcpMetadata.gcpMetadata("https://api.workos.com");
 * ```
 *
 * The provider makes one request per call, with a 10-second timeout and no
 * redirects. It does not cache and does not retry; the SDK calls it again
 * when an exchange attempt fails.
 */
fun gcpMetadata(audience: String): IdentityTokenProvider = gcpMetadata(audience, METADATA_BASE_URL)

/** [gcpMetadata] against another base URL. For tests only; not a public option, and hidden from Java. */
@JvmSynthetic
internal fun gcpMetadata(
  audience: String,
  baseUrl: String
): IdentityTokenProvider {
  require(audience.isNotBlank()) { "audience is required" }
  val http =
    HttpClient
      .newBuilder()
      .followRedirects(HttpClient.Redirect.NEVER)
      .connectTimeout(TIMEOUT)
      .build()
  val request =
    HttpRequest
      .newBuilder(URI.create("$baseUrl$IDENTITY_PATH?audience=${percentEncode(audience)}&format=full"))
      .timeout(TIMEOUT)
      .header("Metadata-Flavor", "Google")
      .GET()
      .build()
  return IdentityTokenProvider {
    val response = http.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) {
      throw IOException("The Google Cloud metadata server returned HTTP ${response.statusCode()}.")
    }
    response.body().trim().takeIf { it.isNotEmpty() }
      ?: throw IOException("The Google Cloud metadata server returned no token.")
  }
}

/** Percent-encode a query value. `URLEncoder` writes a space as `+`, which is form encoding; a URL wants `%20`. */
private fun percentEncode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8).replace("+", "%20")
