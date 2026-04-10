package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class AuthorizationResourceList(
  @JvmField
  @JsonProperty("data")
  val data: List<AuthorizationResource>,
  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
