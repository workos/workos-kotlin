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
  @JvmField val status: Int,
  @JvmField val requestId: String?,
  @JvmField val code: String?,
  message: String?,
  @JvmField val errors: List<Map<String, Any?>>?,
  @JvmField val rawBody: String?,
  cause: Throwable? = null
) : RuntimeException(message ?: "WorkOS API error (status=$status)", cause)
