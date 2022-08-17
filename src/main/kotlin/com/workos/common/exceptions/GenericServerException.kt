package com.workos.common.exceptions

/**
 * Thrown when the API encounters an unexpected error.
 *
 * @param message The error message.
 * @param status The HTTP status code.
 * @param requestId The ID of the correlating request specified in the 'X-Request-ID' header.
 */
class GenericServerException(
  override val message: String?,
  val status: Int,
  val requestId: String
) : Exception(message)
