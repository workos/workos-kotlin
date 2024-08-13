package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateResourceOptions @JvmOverloads constructor(
  /**
   * The type of the resource.
   */
  @JsonProperty("resource_type")
  val resourceType: String,

  /**
   * The unique ID of the resource. If one is not provided, a unique ID will be generated.
   */
  @JsonProperty("resource_id")
  val resourceId: String? = null,

  /**
   * A JSON object containing additional information about the resource.
   */
  @JsonProperty("meta")
  val meta: Map<String, String>? = null
) {
  init {
    require(resourceType.isNotBlank()) { "Resource type is required" }
  }
}
