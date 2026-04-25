// @oagen-ignore-file
package com.workos.common.exceptions

/** Raised for HTTP 422 responses. */
class UnprocessableEntityException(
  requestId: String?,
  code: String?,
  message: String?,
  errors: List<Map<String, Any?>>?,
  rawBody: String?
) : WorkOSException(422, requestId, code, message, errors, rawBody)
