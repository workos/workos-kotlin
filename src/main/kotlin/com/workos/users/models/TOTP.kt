package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class TOTP @JsonCreator constructor(
  @JvmField
  @JsonProperty("issuer")
  val issuer: String,

  @JvmField
  @JsonProperty("user")
  val user: String,

)
