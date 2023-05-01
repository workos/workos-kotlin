package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a WorkOS Organization resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id The unique identifier for the Organization.
 * @param name The name of the Organization.
 * @param allowProfilesOutsideOrganization Whether the Connections within this Organization should allow Profiles that do not have a domain that is present in the set of the Organization's User Email Domains.
 * @param domains List of [OrganizationDomain]s.
 * @param createdAt The timestamp of when the Organization was created.
 * @param updatedAt The timestamp of when the Organization was updated.
 */
data class Organization
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "organization",

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  @JsonProperty("allow_profiles_outside_organization")
  val allowProfilesOutsideOrganization: Boolean,

  @JvmField
  val domains: List<OrganizationDomain>,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
