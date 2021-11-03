package com.workos.common.http

class UnprocessableEntityExceptionResponse {
  class EntityError {
    val field: String? = null

    val code: String? = null
  }
  val message: String? = null

  val errors: List<EntityError> = emptyList()
}
