
package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class ConnectionList
@JsonCreator constructor(
  val data: List<Connection>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata,
)
