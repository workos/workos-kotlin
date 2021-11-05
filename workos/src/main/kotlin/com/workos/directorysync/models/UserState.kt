package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of states for a Directory [User].
 *
 * @param state The User State string value.
 */
enum class UserState(@JsonValue val state: String) {
  /**
   * The user is active.
   */
  Active("active"),
  /**
   * The user is suspended.
   */
  Suspended("suspended")
}
