package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Factor] and its json properties.
 */
data class Sms
@JsonCreator constructor(
  @JvmField
  @JsonProperty("phone_number")
  val phone_number: String
)
