package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationDomain
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String,

  val id: String,

  val domain: String,
)
