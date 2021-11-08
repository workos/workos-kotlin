package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Represents a response from sending a passwordless session.
 * This class is not meant to be instantiated directly.
 *
 * @param success Whether or not the passwordless session was sent successfully.
 */
data class SendSessionResponse @JsonCreator constructor(
  @JvmField
  val success: Boolean
)
