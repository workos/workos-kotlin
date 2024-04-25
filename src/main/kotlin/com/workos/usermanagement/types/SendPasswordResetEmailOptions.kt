package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

class SendPasswordResetEmailOptions(
  /**
   * The email address of the user who requested to reset their password.
   */
  @JsonProperty("email")
  val email: String,

  /**
   * The URL to include in the email.
   */
  @JsonProperty("password_reset_url")
  val passwordResetUrl: String,
) {
  init {
    require(email.isNotBlank()) { "Email is required" }
    require(passwordResetUrl.isNotBlank()) { "Password reset URL is required" }
  }
}
