package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonProperty

class SetRolePermissionsOptions(
  @JsonProperty("permissions")
  val permissions: List<String>
)
