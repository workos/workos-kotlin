package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata
import com.workos.usermanagement.models.OrganizationMembership

data class OrganizationMembershipList
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("data")
  val data: List<OrganizationMembership>,

  @JvmField
  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata
)
