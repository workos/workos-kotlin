package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Factor] and its json properties.
 */
data class Sms(
  @JvmField
  @JsonProperty("phone_number")
  val phoneNumber: String
)
