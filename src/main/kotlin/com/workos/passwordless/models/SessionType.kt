package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of passwordless session types.
 *
 * @param state The Session Type string value.
 */
enum class SessionType(@JsonValue val state: String) {
  /**
   * Magic Link.
   */
  MagicLink("MagicLink"),
}
