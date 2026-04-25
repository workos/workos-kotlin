// @oagen-ignore-file
package com.workos.common.exceptions

/** Fallback used for unexpected status codes or transport failures. */
class GenericException(
  status: Int,
  requestId: String?,
  code: String?,
  message: String?,
  rawBody: String?,
  cause: Throwable? = null
) : WorkOSException(status, requestId, code, message, null, rawBody, cause)
