package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A Magic Auth code that allows the recipient to authenticate to your app
 *
 * @param id The unique ID of the Magic Auth code.
 * @param userId The unique ID of the user.
 * @param email The email address of the user.
 * @param expiresAt The timestamp when the Magic Auth code will expire.
 * @param code The Magic Auth code.
 * @param createdAt The timestamp when the Magic Auth code was created.
 * @param updatedAt The timestamp when the Magic Auth code was last updated.
 */
data class MagicAuth @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("user_id")
  val userId: String,

  @JsonProperty("email")
  val email: String,

  @JsonProperty("expires_at")
  val expiresAt: String,

  @JsonProperty("code")
  val code: String,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String
)
