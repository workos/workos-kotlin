package com.workos.common.exceptions

/**
 * Thrown when the request to a given resource is not authorized.
 *
 * @param message The error message.
 * @param requestId The ID of the correlating request specified in the 'X-Request-ID' header.
 */
class UnauthorizedException(
  override val message: String?,
  private val requestId: String
) : Exception(message) {
  private val status = 401
}
