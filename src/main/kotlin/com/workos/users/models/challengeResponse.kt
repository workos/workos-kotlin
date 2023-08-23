package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class challengeResponse @JsonCreator constructor(

  @JvmField
  @JsonProperty("token")
  val token: String,

  @JvmField
  @JsonProperty("user")
  val user: User,
)
