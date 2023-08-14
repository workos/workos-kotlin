package com.workos.users.models
import com.workos.users.models.UserType

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of user types.
 *
 * @param type The User Type string value.
 */
enum class UserType(@JsonValue val type: String) {
  /**
   * Managed user.
   */
  MANAGED("managed"),

  /**
   * Unmanaged user.
   */
  UNMANAGED("unmanaged")
}
