package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RoleAssignmentResource
@JsonCreator constructor(
  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("external_id")
  val externalId: String,

  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String
)
