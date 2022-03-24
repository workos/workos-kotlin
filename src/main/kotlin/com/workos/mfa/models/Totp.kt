package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents [Totp] and its json properties.
 */
data class Totp
@JsonCreator constructor(
  @JvmField
  @JsonProperty("qr_code")
  val qr_code: String,

  @JvmField
  @JsonProperty("secret")
  val secret: String
)
