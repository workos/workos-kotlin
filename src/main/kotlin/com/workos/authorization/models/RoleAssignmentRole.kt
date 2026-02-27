package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator

data class RoleAssignmentRole
@JsonCreator constructor(
  @JvmField
  val slug: String
)
