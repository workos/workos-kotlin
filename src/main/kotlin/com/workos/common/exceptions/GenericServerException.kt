// @oagen-ignore-file
package com.workos.common.exceptions

/** Raised for HTTP 5xx responses. */
class GenericServerException(
  status: Int,
  requestId: String?,
  code: String?,
  message: String?,
  rawBody: String?,
  cause: Throwable? = null
) : WorkOSException(status, requestId, code, message, null, rawBody, cause)
