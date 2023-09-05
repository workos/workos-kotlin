package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class EnrollAuthFactorResponse @JsonCreator constructor(
  @JvmField
  @JsonProperty("authentication_factor")
  val authenticationFactor: AuthenticationFactor,

  @JvmField
  @JsonProperty("authentication_challenge")
  val authenticationChallenge: AuthenticationChallenge,
)
