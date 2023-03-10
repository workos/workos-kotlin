package com.workos.common.exceptions

/**
 * Thrown when the API encounters a bad request.
 *
 * @param code The optional error code.
 * @param message The error message.
 * @param status The HTTP status code.
 * @param requestId The ID of the correlating request specified in the 'X-Request-ID' header.
 */
class BadRequestException(
  override val message: String?,
  val code: String?,
  val errors: List<Map<String, Any>>?,
  val requestId: String
) : Exception(message) {
  val status = 400
}
