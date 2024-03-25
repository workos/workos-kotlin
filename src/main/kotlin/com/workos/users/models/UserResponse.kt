package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse @JsonCreator constructor(

  @JvmField
  @JsonProperty("user")
  val user: User,

)
