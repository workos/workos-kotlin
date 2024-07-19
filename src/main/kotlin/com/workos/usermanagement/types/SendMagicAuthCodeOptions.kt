package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

class SendMagicAuthCodeOptions(
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
