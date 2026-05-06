// @oagen-ignore-file
package com.workos.common.exceptions

/**
 * Thrown when the API returns an HTTP status the SDK doesn't have a specific
 * exception type for.
 *
 * Distinct from [TransportException], which is thrown when the SDK never received
 * an HTTP response at all (timeout, DNS, TLS, IO error).
 */
class WorkOSGenericException(
  status: Int,
  requestId: String?,
  code: String?,
  message: String?,
  rawBody: String?,
  cause: Throwable? = null
) : WorkOSException(status, requestId, code, message, null, rawBody, cause)
