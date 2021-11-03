package com.workos.organizations.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationDomain
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String,

  @JvmField
  val id: String,

  @JvmField
  val domain: String,
)
