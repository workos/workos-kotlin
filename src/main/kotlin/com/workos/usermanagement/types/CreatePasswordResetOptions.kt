package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

class CreatePasswordResetOptions(
  /**
   * The email address of the user.
   */
  @JsonProperty("email")
  val email: String,
) {
  init {
    require(email.isNotBlank()) { "Email is required" }
  }
}
