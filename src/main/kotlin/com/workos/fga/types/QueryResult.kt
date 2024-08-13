package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order
import com.workos.fga.models.Warrant

@JsonInclude(JsonInclude.Include.NON_NULL)
class QueryResult @JvmOverloads constructor(
  @JsonProperty("resource_type")
  val resourceType: String,

  @JsonProperty("resource_id")
  val resourceId: String,

  @JsonProperty("relation")
  val relation: String,

  @JsonProperty("warrant")
  val warrant: Warrant,

  @JsonProperty("is_implicit")
  val isImplicit: Boolean,

  @JsonProperty("meta")
  val meta: Map<String, String>? = null
) {
  init {
    require(resourceType.isNotBlank()) { "Resource type is required" }
    require(resourceId.isNotBlank()) { "Resource id is required" }
    require(relation.isNotBlank()) { "Relation is required" }
  }
}
