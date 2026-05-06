// @oagen-ignore-file
package com.workos.common.exceptions

/** Raised for HTTP 400 responses. */
class BadRequestException(
  requestId: String?,
  code: String?,
  message: String?,
  errors: List<ApiError>?,
  rawBody: String?
) : WorkOSException(400, requestId, code, message, errors, rawBody)
