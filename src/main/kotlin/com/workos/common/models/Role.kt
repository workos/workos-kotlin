package com.workos.common.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A unique user Role.
 *
 * @param slug The unique role identifier.
 */
data class Role @JsonCreator constructor(
  @JsonProperty("slug")
  val slug: String
)