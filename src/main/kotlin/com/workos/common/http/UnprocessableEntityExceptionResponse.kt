package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonCreator

internal data class UnprocessableEntityExceptionResponse
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val message: String?,
  val code: String?,
  val errors: List<EntityError> = emptyList()
)
