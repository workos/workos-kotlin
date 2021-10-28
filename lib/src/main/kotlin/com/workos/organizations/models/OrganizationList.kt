package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.ListMetadata

data class OrganizationList
@JsonCreator constructor(
  val data: List<Organization>,

  @JsonProperty("list_metadata")
  val listMetadata: ListMetadata,
)
