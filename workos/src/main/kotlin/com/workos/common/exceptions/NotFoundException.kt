package com.workos.common.exceptions

/**
 * Thrown when a resource is not found.
 *
 * @param path The path of the requested resource.
 * @param requestId  The ID of the correlating request specified in the 'X-Request-ID' header.
 */
class NotFoundException(
  private val path: String,
  private val requestId: String,
) : Exception("NotFoundException") {
  private val status = 404
}
