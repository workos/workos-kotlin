package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata
import com.workos.fga.types.QueryResult

data class QueryResponse
@JsonCreator constructor(
  @JsonProperty("data")
  val data: List<QueryResult>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
