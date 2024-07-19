package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A password reset token that allows the recipient to reset their password
 *
 * @param id The unique ID of the password reset token.
 * @param userId The unique ID of the user.
 * @param email The email address of the user.
 * @param passwordResetToken The token for password reset.
 * @param passwordResetUrl The URL where the user can reset their password.
 * @param expiresAt The timestamp when the password reset token will expire.
 * @param createdAt The timestamp when the password reset token was created.
 */
data class PasswordReset @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("user_id")
  val userId: String,

  @JsonProperty("email")
  val email: String,

  @JsonProperty("password_reset_token")
  val passwordResetToken: String,

  @JsonProperty("password_reset_url")
  val passwordResetUrl: String,

  @JsonProperty("expires_at")
  val expiresAt: String,

  @JsonProperty("created_at")
  val createdAt: String
)
