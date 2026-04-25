// @oagen-ignore-file
package com.workos.common.exceptions

/**
 * Raised for HTTP 429 responses. [retryAfterSeconds] is populated from the
 * `Retry-After` header when the server provides one.
 */
class RateLimitException(
  requestId: String?,
  code: String?,
  message: String?,
  /** Seconds to wait before retrying, parsed from the `Retry-After` header, or `null` if absent. */
  @JvmField val retryAfterSeconds: Long?,
  rawBody: String?
) : WorkOSException(429, requestId, code, message, null, rawBody)
