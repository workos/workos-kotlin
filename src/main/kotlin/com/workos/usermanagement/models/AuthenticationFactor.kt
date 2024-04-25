package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An authentication factor
 *
 * @param id The unique ID of the factor.
 * @param createdAt The timestamp when the factor was created.
 * @param updatedAt The timestamp when the factor was last updated.
 * @param type The type of the factor to enroll. The only available option is TOTP.
 * @param totp Time-based one-time password (see [AuthenticationTotp]).
 * @param userId The ID of the user.
 */
data class AuthenticationFactor @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String,

  @JsonProperty("type")
  val type: String,

  @JsonProperty("totp")
  val totp: AuthenticationTotp? = null,

  @JsonProperty("user_id")
  val userId: String? = null
)
