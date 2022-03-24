package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Factor] and its json properties.
 */
data class Factor
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val object: String = "authentication_factor",

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("created_at")
  val created_at: String,

  @JvmField
  @JsonProperty("updated_at")
  val id: String,

  @JvmField
  @JsonProperty("type")
  val id: String,

  @JvmField
  @JsonProperty("environment_id")
  val id: String,

  @JvmField
  @JsonProperty("sms")
  val id: Sms,

  @JvmField
  @JsonProperty("totp")
  val id: Totp,
)
