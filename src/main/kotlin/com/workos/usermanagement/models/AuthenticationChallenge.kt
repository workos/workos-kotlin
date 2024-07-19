package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An authentication challenge
 *
 * @param id The unique ID of the challenge.
 * @param createdAt The timestamp when the challenge was created.
 * @param updatedAt The timestamp when the challenge was last updated.
 * @param expiresAt The timestamp when the challenge will expire.
 * @param authenticationFactorId The unique ID of the authentication factor the challenge belongs to.
 */
data class AuthenticationChallenge @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String,

  @JsonProperty("expires_at")
  val expiresAt: String? = null,

  @JsonProperty("authentication_factor_id")
  val authenticationFactorId: String
)
