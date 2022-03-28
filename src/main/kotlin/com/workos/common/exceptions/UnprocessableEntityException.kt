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
  private val code: String?,
  private val errors: List<EntityError>?,
  private val requestId: String,
) : Exception(message) {
  private val status = 422
}
