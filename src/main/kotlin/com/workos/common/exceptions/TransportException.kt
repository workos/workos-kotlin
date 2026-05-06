// @oagen-ignore-file
package com.workos.common.exceptions

/**
 * Thrown when the SDK can't reach the API at all (timeout, DNS, TLS, IO error).
 *
 * Distinct from [WorkOSGenericException], which represents an unexpected HTTP status
 * returned by the API. A `TransportException` always carries `status = 0` because no
 * HTTP response was ever received.
 */
class TransportException(
  message: String,
  cause: Throwable?
) : WorkOSException(
    status = 0,
    requestId = null,
    code = "transport_error",
    message = message,
    errors = null,
    rawBody = null,
    cause = cause
  )
