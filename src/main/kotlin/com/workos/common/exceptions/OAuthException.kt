package com.workos.common.exceptions

/**
 * Thrown when the API returns an OAuth error (e.g., invalid_grant).
 *
 * @param error The OAuth error code.
 * @param errorDescription The OAuth error description.
 * @param requestId The ID of the correlating request specified in the 'X-Request-ID' header.
 */
class OAuthException(
  val error: String?,
  val errorDescription: String?,
  val requestId: String
) : Exception(errorDescription)
