package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonProperty

class GetResourceOptions @JvmOverloads constructor(
  /**
   * The type of the resource.
   */
  @JsonProperty("resource_type")
  val resourceType: String,

  /**
   * The unique ID of the resource to be fetched.
   */
  @JsonProperty("resource_id")
  val resourceId: String,
) {
  init {
    require(resourceType.isNotBlank()) { "Resource type is required" }
    require(resourceId.isNotBlank()) { "Resource id is required" }
  }
}
