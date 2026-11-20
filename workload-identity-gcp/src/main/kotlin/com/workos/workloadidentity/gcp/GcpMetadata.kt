// @oagen-ignore-file
@file:JvmName("GcpMetadata")

package com.workos.workloadidentity.gcp

import com.workos.workloadidentity.IdentityTokenProvider
import java.io.IOException
import java.math.BigDecimal
import java.net.ConnectException
import java.net.URI
import java.net.URLEncoder
import java.net.UnknownHostException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException
import java.nio.channels.UnresolvedAddressException
import java.time.Duration

private const val METADATA_BASE_URL = "http://metadata.google.internal"

private const val IDENTITY_PATH = "/computeMetadata/v1/instance/service-accounts/default/identity"

private val TIMEOUT: Duration = Duration.ofSeconds(10)

/** How many causes of a failed request are read for the reason. A cause chain can loop. */
private const val CAUSE_LIMIT = 16

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
 *
 * The request never goes through a proxy, whatever the JVM's proxy settings
 * are (`http.proxyHost`, `java.net.useSystemProxies`, or a default
 * `ProxySelector`).
 *
 * When the metadata server cannot be reached, the provider throws an
 * `IOException` whose message gives the reason, such as a host name that
 * does not resolve, and says that the provider works only on Google Cloud.
 * The original exception is its cause.
 */
fun gcpMetadata(audience: String): IdentityTokenProvider = gcpMetadata(audience, METADATA_BASE_URL)

/** [gcpMetadata] against another base URL and timeout. For tests only; not a public option, and hidden from Java. */
@JvmSynthetic
internal fun gcpMetadata(
  audience: String,
  baseUrl: String,
  timeout: Duration = TIMEOUT
): IdentityTokenProvider {
  require(audience.isNotBlank()) { "audience is required" }
  // The metadata server is an address inside the workload's own VM or
  // container. A proxy is another machine: it cannot reach that server, and it
  // could answer with its own identity. Without `proxy(...)` the client would
  // ask the JVM's default ProxySelector, so it is told to use none.
  val http =
    HttpClient
      .newBuilder()
      .followRedirects(HttpClient.Redirect.NEVER)
      .connectTimeout(timeout)
      .proxy(HttpClient.Builder.NO_PROXY)
      .build()
  val request =
    HttpRequest
      .newBuilder(URI.create("$baseUrl$IDENTITY_PATH?audience=${percentEncode(audience)}&format=full"))
      .timeout(timeout)
      .header("Metadata-Flavor", "Google")
      .GET()
      .build()
  val host = request.uri().host ?: baseUrl
  return IdentityTokenProvider {
    val response =
      try {
        http.send(request, HttpResponse.BodyHandlers.ofString())
      } catch (e: IOException) {
        throw unreachable(e, host, timeout)
      }
    if (response.statusCode() != 200) {
      throw IOException("The Google Cloud metadata server returned HTTP ${response.statusCode()}.")
    }
    response.body().trim().takeIf { it.isNotEmpty() }
      ?: throw IOException("The Google Cloud metadata server returned no token.")
  }
}

/**
 * The error for a request that got no response from the metadata server at
 * [host] within [timeout]. [failure] is kept as the cause. Hidden from Java.
 */
@JvmSynthetic
internal fun unreachable(
  failure: IOException,
  host: String,
  timeout: Duration
): IOException =
  IOException(
    "The GCP metadata server could not be reached: ${unreachableReason(failure, host, timeout)}. " +
      "This provider works only on Google Cloud (Compute Engine, GKE, Cloud Run, Cloud Functions).",
    failure
  )

/**
 * Why the request got no response, in words. `java.net.http` often reports a
 * failure with exceptions that have no message, so the reason is read from
 * the types and messages of the whole cause chain. The simple class name of
 * the root cause is the last resort.
 */
private fun unreachableReason(
  failure: IOException,
  host: String,
  timeout: Duration
): String {
  val chain = generateSequence<Throwable>(failure) { it.cause }.take(CAUSE_LIMIT).toList()
  // An exception built from a cause alone takes the cause's `toString()` as
  // its message. That is a class name again, so it does not count.
  val message =
    chain.firstNotNullOfOrNull { e ->
      e.message
        ?.takeUnless { it == e.cause?.toString() }
        ?.trim()
        ?.trimEnd('.')
        ?.takeIf(String::isNotBlank)
    }
  return when {
    chain.any { it is UnresolvedAddressException || it is UnknownHostException } -> "$host could not be resolved"
    // HttpConnectTimeoutException is an HttpTimeoutException. The connect
    // timeout and the request timeout are the same length.
    chain.any { it is HttpTimeoutException } -> "timed out after ${inSeconds(timeout)} seconds"
    // A refused connection is a ConnectException. The message is "Connection
    // refused" when there is one. On JDK 17, on Linux and on macOS, a
    // connection that is refused at once has no message anywhere in the chain.
    chain.any { it is ConnectException } && (message == null || message.contains("refused", ignoreCase = true)) ->
      "connection refused"
    else -> message ?: chain.last().javaClass.let { it.simpleName.ifEmpty { it.name } }
  }
}

/** A duration as a number of seconds with no trailing zeros: `10`, or `0.25` for 250 milliseconds. */
private fun inSeconds(duration: Duration): String = BigDecimal.valueOf(duration.toMillis(), 3).stripTrailingZeros().toPlainString()

/** Percent-encode a query value. `URLEncoder` writes a space as `+`, which is form encoding; a URL wants `%20`. */
private fun percentEncode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8).replace("+", "%20")
