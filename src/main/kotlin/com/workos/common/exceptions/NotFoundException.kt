// @oagen-ignore-file
package com.workos.common.exceptions

/** Raised for HTTP 404 responses. */
class NotFoundException(
  requestId: String?,
  code: String?,
  message: String?,
  /** The request URL path that was not found. */
  @JvmField val path: String?,
  rawBody: String?
) : WorkOSException(404, requestId, code, message, null, rawBody)
