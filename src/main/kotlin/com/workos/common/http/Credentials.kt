// @oagen-ignore-file
package com.workos.common.http

import java.util.concurrent.TimeoutException

/**
 * A source of authorization headers for API requests.
 *
 * A client built with [com.workos.workloadidentity.WorkloadIdentity.client]
 * asks its credentials for a [CredentialSnapshot] before every HTTP attempt,
 * including retries, so an attempt never carries an expired credential.
 */
interface Credentials {
  /**
   * Return the headers for one HTTP attempt. May block while a credential is
   * fetched.
   *
   * @param timeoutMillis the longest the caller will wait, or `null` when the
   *   caller sets no limit.
   * @throws TimeoutException when [timeoutMillis] passes before a credential
   *   is available.
   * @throws InterruptedException when the calling thread is interrupted while
   *   it waits.
   */
  @Throws(TimeoutException::class, InterruptedException::class)
  fun resolve(timeoutMillis: Long?): CredentialSnapshot

  /**
   * Mark the credential identified by [generation] as unusable, after the API
   * answered it with a 401. Has no effect when a newer credential is already
   * in place. Never blocks on the network.
   */
  fun invalidate(generation: Long)
}

/**
 * The authorization headers for one HTTP attempt, with the identity of the
 * credential they came from.
 */
class CredentialSnapshot(
  /** Headers to set on the attempt. They replace any header of the same name already on the request. */
  @JvmField val headers: Map<String, String>,
  /** Identifies the credential the headers came from. Pass it to [Credentials.invalidate] after a 401. */
  @JvmField val generation: Long,
  /** `true` when a 401 on this snapshot is worth one more [Credentials.resolve]. */
  @JvmField val recoverable: Boolean
) {
  /** Describes the snapshot without its header values, so a credential never reaches a log line. */
  override fun toString(): String = "CredentialSnapshot(generation=$generation, recoverable=$recoverable)"
}

/**
 * Credential state for one logical request. [BaseClient] creates one before
 * its retry loop and attaches it to every attempt as an OkHttp request tag,
 * because each attempt is a separate OkHttp call and a credentials
 * interceptor would otherwise start from scratch on every retry.
 */
internal class CredentialRecovery(
  /** `true` when the request carries a per-request API key or access token, which is sent as built. */
  val authorizationOverridden: Boolean
) {
  /** Set when the request has spent its single 401 recovery. Never cleared. */
  var used: Boolean = false
}
