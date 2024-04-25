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
   * The unique role identifier. Defaults to `member`.
   */
  @JsonProperty("role_slug")
  val roleSlug: String? = null
) {
  init {
    require(userId.isNotBlank()) { "User ID is required" }
    require(organizationId.isNotBlank()) { "Organization ID is required" }
  }
}
