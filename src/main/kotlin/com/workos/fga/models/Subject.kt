package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A WorkOS FGA subject
 *
 * @param resourceType The type of the resource. Must be one of your system's existing resource types.
 * @param resourceId The unique ID of the resource.
 * @param relation Specifies a relation required on the resource to be part of the subject group.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Subject @JsonCreator constructor(
  @JsonProperty("resource_type")
  val resourceType: String,

  @JsonProperty("resource_id")
  val resourceId: String,

  @JsonProperty("relation")
  val relation: String? = null,
)
