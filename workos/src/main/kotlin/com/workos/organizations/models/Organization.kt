package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

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
  val updatedAt: String,
)
