package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An email verification code that allows the recipient to verify their email
 *
 * @param id The unique ID of the email verification code.
 * @param userId The unique ID of the user.
 * @param email The email address of the user.
 * @param expiresAt The timestamp when the email verification code will expire.
 * @param createdAt The timestamp when the email verification code was created.
 * @param updatedAt The timestamp when the email verification code was last updated.
 */
data class EmailVerificationEventData @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("user_id")
  val userId: String,

  @JsonProperty("email")
  val email: String,

  @JsonProperty("expires_at")
  val expiresAt: String,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String
)
