package com.workos.common.http

internal data class BadRequestExceptionResponse(
  val message: String? = null,
  val code: String? = null,
  val errors: List<Map<String, Any>>? = null
)
