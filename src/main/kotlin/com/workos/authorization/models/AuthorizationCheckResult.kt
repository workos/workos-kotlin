package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator

data class AuthorizationCheckResult
@JsonCreator constructor(
  @JvmField
  val authorized: Boolean
)
