package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GenericErrorResponse @JsonCreator constructor(
  val error: String?,
  @JsonProperty("error_description")
  val errorDescription: String?,
  val message: String?,
  val code: String?,
)
