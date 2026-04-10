package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [VerifyChallengeResponse] in both successfull and error responses.
 */
data class VerifyChallengeResponse(
  @JvmField
  @JsonProperty("challenge")
  val challenge: Challenge,
  @JvmField
  @JsonProperty("valid")
  val valid: Boolean
)
