package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

class ResetPasswordOptions(
  /**
   * The token query parameter from the password reset URL.
   */
  @JsonProperty("token")
  val token: String,

  /**
   * The new password to set for the user.
   */
  @JsonProperty("new_password")
  val newPassword: String,
) {
  init {
    require(token.isNotBlank()) { "Token is required" }
    require(newPassword.isNotBlank()) { "New Password is required" }
  }
}
