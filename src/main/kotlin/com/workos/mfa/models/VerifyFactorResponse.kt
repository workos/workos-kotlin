package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [VerifyFactorResponse] in both successfull and error responses.
 */

@Deprecated("Please use `verifyChallenge` instead")
data class VerifyFactorResponse(
  @JvmField
  @JsonProperty("challenge")
  val challenge: Challenge,
  @JvmField
  @JsonProperty("valid")
  val valid: Boolean
)
