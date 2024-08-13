package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class WriteWarrantResponse @JsonCreator constructor(
  @JsonProperty("warrant_token")
  val warrantToken: String
)
