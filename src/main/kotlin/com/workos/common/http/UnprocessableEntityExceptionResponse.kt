package com.workos.common.http

internal data class UnprocessableEntityExceptionResponse(
  val message: String?,
  val code: String?,
  val errors: List<EntityError> = emptyList()
)
