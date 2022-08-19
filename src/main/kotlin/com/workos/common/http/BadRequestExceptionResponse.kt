package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonCreator

internal data class BadRequestExceptionResponse
@JsonCreator constructor(
  val message: String? = null,

  val code: String? = null,

  val errors: List<Map<String, Any>>? = null,
)
