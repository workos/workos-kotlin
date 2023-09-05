package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator

data class AuthenticationFactorList
@JsonCreator constructor(
  @JvmField
  val data: List<AuthenticationFactor>,
)
