package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Organization @JsonCreator constructor(

  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("name")
  val name: String,
)
