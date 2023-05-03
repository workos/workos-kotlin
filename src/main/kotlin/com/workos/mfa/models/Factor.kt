package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Factor] and its json properties.
 */
data class Factor
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "authentication_factor",

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
  @JsonProperty("type")
  val type: String,

  @JvmField
  @JsonProperty("sms")
  val sms: Sms?,

  @JvmField
  @JsonProperty("totp")
  val totp: Totp?
)
