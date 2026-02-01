package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.usermanagement.types.OrganizationMembershipStatusEnumType

/**
 * An organization membership is a top-level resource that represents a [User]'s relationship
 * with an organization. A user may be a member of zero, one, or many organizations.
 *
 * @param id The unique ID of the organization membership.
 * @param userId The ID of the [User].
 * @param organizationId The ID of the organization which the user belongs to.
 * @param role The role in the organization membership (see [OrganizationMembershipRole]).
 * @param roles The roles in the organization membership for multiple roles support (see [OrganizationMembershipRole]).
 * @param status Whether the organization membership is active or pending (see enum values in [OrganizationMembershipStatusEnumType]).
 * @param createdAt The timestamp when the user was created.
 * @param updatedAt The timestamp when the user was last updated.
 * @param idpAttributes Custom attributes from the identity provider.
 */
data class OrganizationMembership @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("user_id")
  val userId: String,

  @JsonProperty("organization_id")
  val organizationId: String,

  @JsonProperty("role")
  val role: OrganizationMembershipRole? = null,

  @JsonProperty("roles")
  val roles: List<OrganizationMembershipRole>? = null,

  @JsonProperty("status")
  val status: OrganizationMembershipStatusEnumType,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String,

  @JsonProperty("idp_attributes")
  val idpAttributes: Map<String, Any>? = null
)
