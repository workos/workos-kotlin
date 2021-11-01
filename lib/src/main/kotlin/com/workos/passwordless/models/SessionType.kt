package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonValue

enum class SessionType(@JsonValue val state: String) {
  MagicLink("MagicLink"),
}
