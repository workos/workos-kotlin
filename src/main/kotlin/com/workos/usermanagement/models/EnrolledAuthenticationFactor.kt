package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An enrolled authentication factor object
 *
 * @param challenge The [AuthenticationChallenge] that is used to complete the authentication process.
 * @param factor The [AuthenticationFactor] that represents the additional authentication method used on top of the existing authentication strategy.
 */
data class EnrolledAuthenticationFactor @JsonCreator constructor(
  @JsonProperty("challenge")
  val challenge: AuthenticationChallenge,

  @JsonProperty("factor")
  val factor: AuthenticationFactor
)
