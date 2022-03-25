package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [VerifyFactorResponse] in both successfull and error responses.
 */

data class VerifyFactorResponse
@JsonCreator constructor(
  @JvmField
  @JsonProperty("challenge")
  val challenge: Challenge,

  @JvmField
  @JsonProperty("valid")
  val valid: Boolean,
)
