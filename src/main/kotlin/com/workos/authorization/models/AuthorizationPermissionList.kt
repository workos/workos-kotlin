package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class AuthorizationPermissionList(
  @JvmField
  @JsonProperty("data")
  val data: List<AuthorizationPermission>,
  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
