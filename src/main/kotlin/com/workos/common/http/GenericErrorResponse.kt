package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonProperty

internal data class GenericErrorResponse(
  val error: String? = null,
  @JsonProperty("error_description")
  val errorDescription: String? = null,
  val message: String? = null
)
