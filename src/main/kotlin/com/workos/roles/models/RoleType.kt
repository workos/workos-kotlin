package com.workos.roles.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of types of [Role]
 *
 * @param type The Role type string value.
 */
enum class RoleType(@JsonValue val type: String) {
  Environment("EnvironmentRole"),
  Organization("OrganizationRole")
}
