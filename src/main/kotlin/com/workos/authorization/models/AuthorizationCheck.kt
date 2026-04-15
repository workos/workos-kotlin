package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationCheck(
  @JvmField
  @JsonProperty("authorized")
  val authorized: Boolean
)
