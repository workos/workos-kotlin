package com.workos.roles.models

/**
 * A list of WorkOS [Role] resources. This class is not meant to be
 * instantiated directly.
 *
 * @param data A list of [Role].
 */
data class RoleList(
  @JvmField
  val data: List<Role>
)
