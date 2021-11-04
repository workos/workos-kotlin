package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class ConnectionList
@JsonCreator constructor(
  @JvmField
  val data: List<Connection>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata,
)
