// @oagen-ignore-file
package com.workos.common.exceptions

/** Raised for HTTP 401 responses. */
class UnauthorizedException(
  requestId: String?,
  code: String?,
  message: String?,
  rawBody: String?
) : WorkOSException(401, requestId, code, message, null, rawBody)
