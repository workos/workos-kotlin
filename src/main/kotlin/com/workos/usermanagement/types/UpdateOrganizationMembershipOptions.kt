package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class UpdateOrganizationMembershipOptions @JvmOverloads constructor(
  /**
   * The id of the organization membership.
   */
  @JsonProperty("id")
  val id: String,

  /**
   * The unique role identifier. Defaults to `member`.
   */
  @JsonProperty("role_slug")
  val roleSlug: String
) {
  init {
    require(id.isNotBlank()) { "Organization Membership ID is required" }
  }
}
