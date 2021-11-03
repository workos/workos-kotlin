package com.workos.common.exceptions

import com.workos.common.http.UnprocessableEntityExceptionResponse.EntityError

class UnprocessableEntityException(
  override val message: String?,
  val errors: List<EntityError>?,
  val requestId: String
) : Exception(message) {
  val status = 422
}
