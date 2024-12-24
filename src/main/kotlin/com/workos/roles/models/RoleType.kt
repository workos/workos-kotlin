package com.workos.roles.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of types of [Role]
 *
 * @param state The Role type string value.
 */
enum class RoleType(@JsonValue val state: String) {
  Environment("EnvironmentRole"),
  Organization("OrganizationRole")
}
