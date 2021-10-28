package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Organization
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String,

  val id: String,

  val name: String,

  @JsonProperty("allow_profiles_outside_organization")
  val allowProfilesOutsideOrganization: Boolean,

  val domains: List<OrganizationDomain>,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String,
)
