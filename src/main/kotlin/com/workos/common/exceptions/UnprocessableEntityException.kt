package com.workos.common.exceptions

import com.workos.common.http.EntityError

/**
 * Thrown when the request body is not understood by the system.
 *
 * @param message The error message.
 * @param errors A list of errors for the given request body.
 * @param requestId The ID of the correlating request specified in the 'X-Request-ID' header.
 */
class UnprocessableEntityException(
  override val message: String?,
  val code: String?,
  val errors: List<EntityError>?,
  val requestId: String
) : Exception(message) {
  val status = 422
}
