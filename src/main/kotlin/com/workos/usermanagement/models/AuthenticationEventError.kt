package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An authentication event error.
 *
 * @param code The error code.
 * @param message The error message.
 */
data class AuthenticationEventError @JsonCreator constructor(
  @JsonProperty("code")
  val issuer: String,

  @JsonProperty("message")
  val user: String
)
