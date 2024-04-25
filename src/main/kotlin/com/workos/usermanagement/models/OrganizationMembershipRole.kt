package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An organization membership role.
 *
 * @param slug The unique role identified.
 */
data class OrganizationMembershipRole @JsonCreator constructor(
  @JsonProperty("slug")
  val slug: String
)
