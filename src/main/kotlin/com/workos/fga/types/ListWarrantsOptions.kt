package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class ListWarrantsOptions @JvmOverloads constructor(
  @JsonProperty("resource_type")
  val resourceType: String? = null,

  @JsonProperty("resource_id")
  val resourceId: String? = null,

  @JsonProperty("relation")
  val relation: String? = null,

  @JsonProperty("subject_type")
  val subjectType: String? = null,

  @JsonProperty("subject_id")
  val subjectId: String? = null,

  @JsonProperty("subject_relation")
  val subjectRelation: String? = null,

  @JsonProperty("limit")
  val limit: Int? = null,

  @JsonProperty("after")
  val after: String? = null
)
