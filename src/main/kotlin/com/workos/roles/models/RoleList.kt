package com.workos.roles.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A list of WorkOS [Role] resources. This class is not meant to be
 * instantiated directly.
 *
 * @param data A list of [Role].
 */
data class RoleList
@JsonCreator constructor(
  @JvmField
  val data: List<Role>,
)
