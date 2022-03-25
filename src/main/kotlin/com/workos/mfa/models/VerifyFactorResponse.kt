package com.workos.mfa.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [VerifyFactorResponse] in both successfull and error responses.
 */

interface VerifyFactorResponse

data class VerifyFactorResponseSuccess
@JsonCreator constructor(
  @JvmField
  @JsonProperty("challenge")
  val challenge: Challenge,

  @JvmField
  @JsonProperty("qr_code")
  val valid: Boolean,
) : VerifyFactorResponse

data class VerifyFactorResponseError
@JsonCreator constructor(
  @JvmField
  @JsonProperty("code")
  val code: String,

  @JvmField
  @JsonProperty("message")
  val message: String,
) : VerifyFactorResponse
