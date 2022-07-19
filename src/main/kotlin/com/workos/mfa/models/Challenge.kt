package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Challenge] and its json properties.
 */
data class Challenge
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "authentication_challenge",

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,

  @JvmField
  @JsonProperty("expires_at")
  val expiresAt: String,

  @JvmField
  @JsonProperty("code")
  val code: String?,

  @JvmField
  @JsonProperty("authentication_factor_id")
  val authenticationauthenticationFactorId: String,
)
