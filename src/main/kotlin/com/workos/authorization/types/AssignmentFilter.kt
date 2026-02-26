package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonValue

enum class AssignmentFilter(@JsonValue val value: String) {
  Direct("direct"),
  Inherited("inherited");

  override fun toString(): String = value
}
