package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MagicAuthChallenge @JsonCreator constructor(

  @JvmField
  @JsonProperty("id")
  val id: String,

)
