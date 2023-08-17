package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class VerifySessionResponse @JsonCreator constructor(

  @JvmField
  @JsonProperty("session")
  val session: Session,

  @JvmField
  @JsonProperty("user")
  val user: User,
)
