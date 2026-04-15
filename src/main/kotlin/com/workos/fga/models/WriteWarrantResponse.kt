package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WriteWarrantResponse(
  @JsonProperty("warrant_token")
  val warrantToken: String
)
