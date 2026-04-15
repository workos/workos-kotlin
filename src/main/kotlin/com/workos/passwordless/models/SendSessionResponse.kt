package com.workos.passwordless.models

/**
 * Represents a response from sending a passwordless session.
 * This class is not meant to be instantiated directly.
 *
 * @param success Whether or not the passwordless session was sent successfully.
 */
data class SendSessionResponse(
  @JvmField
  val success: Boolean
)
