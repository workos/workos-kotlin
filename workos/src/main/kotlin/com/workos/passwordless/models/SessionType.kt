package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of passwordless session types.
 */
enum class SessionType(@JsonValue val state: String) {
  /**
   * Magic Link.
   */
  MagicLink("MagicLink"),
}
