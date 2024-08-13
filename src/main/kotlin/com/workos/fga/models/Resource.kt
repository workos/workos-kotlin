package com.workos.fga.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A WorkOS resource
 *
 * @param resourceType The type of the resource.
 * @param resourceId The unique ID of the resource.
 * @param meta A JSON object containing additional information about the resource.
 */
data class Resource @JsonCreator constructor(
  @JsonProperty("resource_type")
  val resourceType: String,

  @JsonProperty("resource_id")
  val resourceId: String,

  @JsonProperty("meta")
  val meta: Map<String, String>? = null
)
