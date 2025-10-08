package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateOrganizationMembershipOptions @JvmOverloads constructor(
  /**
   * The id of the user.
   */
  @JsonProperty("user_id")
  val userId: String,

  /**
   * The id of the organization.
   */
  @JsonProperty("organization_id")
  val organizationId: String,

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
    require(userId.isNotBlank()) { "User ID is required" }
    require(organizationId.isNotBlank()) { "Organization ID is required" }
  }
}
