package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.usermanagement.types.OrganizationMembershipStatusEnumType

/**
 * A slim organization membership returned by the Authorization API.
 *
 * Unlike the full [com.workos.usermanagement.models.OrganizationMembership],
 * this model omits role, custom_attributes, and other fields not returned
 * by the authorization endpoints.
 */
data class SlimOrganizationMembership(
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
  val updatedAt: String
)
