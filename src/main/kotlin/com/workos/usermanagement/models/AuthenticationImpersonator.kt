package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An authentication impersonator object.
 *
 * @param email The email address of the WorkOS Dashboard user who is impersonating the user.
 * @param reason The justification the impersonator gave for impersonating the user.
 */
data class AuthenticationImpersonator @JsonCreator constructor(
  @JsonProperty("email")
  val email: String,

  @JsonProperty("reason")
  val reason: String? = null
)
