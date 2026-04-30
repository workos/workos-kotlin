package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.usermanagement.models.User
import com.workos.usermanagement.types.OrganizationMembershipStatusEnumType

/**
 * A slim organization membership returned by the Authorization API.
 *
 * Unlike the full [com.workos.usermanagement.models.OrganizationMembership],
 * this model omits role, custom_attributes, and other fields not returned
 * by the authorization endpoints.
 *
 * The [user] field is only populated by endpoints that explicitly include
 * the full user object in their response (currently
 * `GET /authorization/resources/:resource_id/organization_memberships` and
 * `GET /authorization/organizations/:organization_id/resources/:resource_type_slug/:external_id/organization_memberships`).
 */
data class SlimOrganizationMembership
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("object")
  val obj: String = "organization_membership",

  @JvmField
  @JsonProperty("user_id")
  val userId: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String,

  @JvmField
  @JsonProperty("status")
  val status: OrganizationMembershipStatusEnumType,

  @JvmField
  @JsonProperty("directory_managed")
  val directoryManaged: Boolean = false,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,

  @JvmField
  @JsonProperty("user")
  val user: User? = null
)
