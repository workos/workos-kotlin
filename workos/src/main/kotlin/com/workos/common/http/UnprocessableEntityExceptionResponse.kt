package com.workos.common.http

import com.fasterxml.jackson.annotation.JsonCreator

internal data class UnprocessableEntityExceptionResponse
@JsonCreator constructor(
  val message: String?,

  val errors: List<EntityError> = emptyList()
)
