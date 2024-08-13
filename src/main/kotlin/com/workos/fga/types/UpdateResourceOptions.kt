package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class UpdateResourceOptions @JvmOverloads constructor(
  /**
   * A JSON object containing additional information about the resource.
   */
  @JsonProperty("meta")
  val meta: Map<String, String>? = null
)
