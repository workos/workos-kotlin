package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents [Totp] and its json properties.
 */
data class Totp
@JsonCreator constructor(
  @JvmField
  @JsonProperty("qr_code")
  val qrCode: String,

  @JvmField
  @JsonProperty("secret")
  val secret: String
)
