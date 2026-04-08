package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationCheck
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("authorized")
  val authorized: Boolean
)
