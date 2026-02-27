package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class AuthorizationResourceList
@JsonCreator constructor(
  @JvmField
  @JsonProperty("data")
  val data: List<AuthorizationResource>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
