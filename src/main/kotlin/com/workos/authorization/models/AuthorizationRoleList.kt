package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class AuthorizationRoleList(
  @JvmField
  @JsonProperty("data")
  val data: List<AuthorizationRole>,
  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata? = null
)
