package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonValue

enum class Assignment(@JsonValue val value: String) {
  Direct("direct"),

  Indirect("indirect");
}
