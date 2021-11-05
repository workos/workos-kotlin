package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator

data class ConnectionDomain
@JsonCreator constructor(
  @JvmField
  val domain: String,

  @JvmField
  val id: String,
)
