package com.workos.common.http

/**
 * Represents a validation error for an entity.
 *
 * @param field The field that the error occurred on.
 * @param code The error code associated with the field.
 */
data class EntityError(
  val field: String? = null,
  val code: String? = null
)
