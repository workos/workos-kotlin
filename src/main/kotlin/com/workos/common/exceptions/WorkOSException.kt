// @oagen-ignore-file
package com.workos.common.exceptions

/**
 * Base class for all typed errors raised by the WorkOS SDK.
 *
 * Every failure from the API or transport layer is translated into a subclass
 * of [WorkOSException], so callers can write a single `catch (e: WorkOSException)`
 * block instead of leaking OkHttp / Jackson exceptions.
 */
sealed class WorkOSException(
  /** HTTP status code returned by the API, or `0` for transport-level failures. */
  @JvmField val status: Int,
  /** Value of the `X-Request-Id` response header, if present. */
  @JvmField val requestId: String?,
  /** Machine-readable error code from the API response body, if present. */
  @JvmField val code: String?,
  message: String?,
  /** Structured validation errors from the API response body, if present. */
  @JvmField val errors: List<Map<String, Any?>>?,
  /** The raw, unparsed response body for debugging. */
  @JvmField val rawBody: String?,
  cause: Throwable? = null
) : RuntimeException(message ?: "WorkOS API error (status=$status)", cause)
