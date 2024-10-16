package com.workos.common.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A unique user role, used with organization memberships and profiles.
 *
 * @param slug The unique role identifier.
 */
data class Role @JsonCreator constructor(
  @JsonProperty("slug")
  val slug: String
)
