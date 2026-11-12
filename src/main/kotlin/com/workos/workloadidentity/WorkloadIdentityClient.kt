// @oagen-ignore-file
package com.workos.workloadidentity

import com.workos.WorkOS
import com.workos.common.http.CredentialRecovery
import com.workos.common.http.CredentialSnapshot
import com.workos.common.http.Credentials
import com.workos.common.http.RetryConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private const val DEFAULT_CALL_TIMEOUT_SECONDS = 30L

/**
 * Entry point for clients that authenticate with workload identity instead
 * of an API key.
 */
object WorkloadIdentity {
  /**
   * Build a [WorkOS] client that authenticates every request with
   * [credentials].
   *
   * Kotlin usage:
   * ```kotlin
   * val workos = WorkloadIdentity.client(credentials)
   * ```
   *
   * Java usage:
   * ```java
   * WorkOS workos = WorkloadIdentity.client(credentials);
   * ```
   *
   * The factory takes no API key, and the client never sends one: its
   * `apiKey` is empty, and `WORKOS_API_KEY` is not read. A failure to obtain
   * a credential surfaces as an error; nothing falls back to a key. Methods
   * that send the API key as a `client_secret`, such as the SSO and User
   * Management code exchanges, therefore cannot be used on this client.
   *
   * A request that passes its own key or access token through
   * [com.workos.common.http.RequestOptions.apiKey] or a per-operation access
   * token is sent with that value, as on any client.
   *
   * @param credentials the credentials to authenticate with, normally a [WorkloadIdentityCredentials].
   * @param clientId optional WorkOS client ID, as on [WorkOS].
   * @param apiBaseUrl base URL for all API requests.
   * @param httpClient the OkHttp client to build on. Its settings and interceptors are kept;
   *   the credentials are added to a copy, so the client passed in is not changed. The token
   *   exchange does not use it: if the token endpoint needs the same proxy or TLS settings,
   *   also pass the client as the `httpClient` of [WorkloadIdentityCredentials].
   * @param retryConfig retry policy applied to transient HTTP failures.
   */
  @JvmStatic
  @JvmOverloads
  fun client(
    credentials: Credentials,
    clientId: String? = null,
    apiBaseUrl: String = WorkOS.DEFAULT_BASE_URL,
    httpClient: OkHttpClient? = null,
    retryConfig: RetryConfig = RetryConfig.DEFAULT
  ): WorkOS {
    val base =
      httpClient
        ?: OkHttpClient
          .Builder()
          .callTimeout(DEFAULT_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
          .build()
    return WorkOS(
      apiKey = "",
      clientId = clientId,
      apiBaseUrl = apiBaseUrl,
      httpClient = base.newBuilder().addInterceptor(CredentialsInterceptor(credentials)).build(),
      retryConfig = retryConfig
    )
  }
}

/**
 * Sets the credential headers on every HTTP attempt and recovers once from a
 * 401.
 *
 * [com.workos.common.http.BaseClient] makes a new OkHttp call for each retry
 * attempt, so this interceptor runs once per attempt. The single recovery a
 * logical request is allowed lives in the [CredentialRecovery] tag that
 * `BaseClient` attaches to every attempt, not here.
 */
internal class CredentialsInterceptor(
  private val credentials: Credentials
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val recovery = request.tag(CredentialRecovery::class.java) ?: CredentialRecovery(authorizationOverridden = false)
    // A per-request key or access token keeps its precedence and is not
    // recoverable: the request goes out exactly as it was built.
    if (recovery.authorizationOverridden) return chain.proceed(request)

    val startedAt = System.nanoTime()
    val callTimeoutNanos = chain.call().timeout().timeoutNanos()

    val snapshot = resolve(callTimeoutNanos, startedAt)
    val response = chain.proceed(request.with(snapshot))
    val recoverable =
      response.code == 401 &&
        snapshot.recoverable &&
        !recovery.used &&
        request.body?.isOneShot() != true
    if (!recoverable) return response

    // Invalidate the generation this attempt used, never "the current one",
    // then send the same request again: same body, same idempotency key.
    recovery.used = true
    response.close()
    credentials.invalidate(snapshot.generation)
    return chain.proceed(request.with(resolve(callTimeoutNanos, startedAt)))
  }

  /**
   * Resolve within what is left of the call's own timeout. OkHttp's call
   * timeout cancels the call but cannot wake a thread waiting on an exchange,
   * so the wait is bounded here. A timeout or an interrupt becomes the I/O
   * failure OkHttp itself raises for them.
   */
  private fun resolve(
    callTimeoutNanos: Long,
    startedAt: Long
  ): CredentialSnapshot {
    val timeoutMillis =
      if (callTimeoutNanos <= 0L) {
        null
      } else {
        TimeUnit.NANOSECONDS.toMillis(callTimeoutNanos - (System.nanoTime() - startedAt)).coerceAtLeast(0L)
      }
    return try {
      credentials.resolve(timeoutMillis)
    } catch (e: TimeoutException) {
      throw InterruptedIOException("timeout waiting for workload identity credentials").apply { initCause(e) }
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      throw InterruptedIOException("interrupted waiting for workload identity credentials").apply { initCause(e) }
    }
  }
}

/** Copy of the request with the snapshot's headers, which replace any header of the same name. */
private fun Request.with(snapshot: CredentialSnapshot): Request {
  val builder = newBuilder()
  snapshot.headers.forEach { (name, value) -> builder.header(name, value) }
  return builder.build()
}
