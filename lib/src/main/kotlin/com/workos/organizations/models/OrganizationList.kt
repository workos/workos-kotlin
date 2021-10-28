package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.workos.common.models.ListMetadata

data class OrganizationList
@JsonCreator constructor(
  val data: List<Organization>,

  val listMetadata: ListMetadata,
)
