package com.workos.authorization.types

import com.fasterxml.jackson.annotation.JsonProperty

class AddRolePermissionOptions(
  @JsonProperty("slug")
  val slug: String
)
