package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a [Profile] and an access token that can be used to
 * manage sessions. This class is not meant to be instantiated directly.
 *
 * @param profile [Profile]
 * @param token An access token that can be used to manage sessions like one would a normal OAuth access token. Access tokens are one-time use and expire 10 minutes after they’re created. Session duration is up to the Developer.
 */

interface VerifyFactorResponse{}

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