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
   * The unique slug of the role to grant to this membership.
   */
  @JsonProperty("role_slug")
  val roleSlug: String? = null,

  /**
   * The unique slugs of the roles to grant to this membership.
   */
  @JsonProperty("role_slugs")
  val roleSlugs: List<String>? = null
) {
  init {
    require(id.isNotBlank()) { "Organization Membership ID is required" }
    require(roleSlug == null || roleSlugs == null) { "Cannot specify both roleSlug and roleSlugs" }
  }
}
