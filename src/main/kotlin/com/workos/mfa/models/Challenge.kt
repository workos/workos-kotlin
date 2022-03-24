package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Challenge] and its json properties.
 */
data class Challenge
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val object: String = "authentication_challenge",

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("created_at")
  val created_at: String,

  @JvmField
  @JsonProperty("updated_at")
  val created_at: String,

  @JvmField
  @JsonProperty("expires_at")
  val created_at: String,

  @JvmField
  @JsonProperty("code")
  val id: String,

  @JvmField
  @JsonProperty("authentication_factor_id")
  val id: String,
)
