package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RoleAssignment
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "role_assignment",

  @JvmField
  val id: String,

  @JvmField
  val role: RoleAssignmentRole,

  @JvmField
  val resource: RoleAssignmentResource,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)

// TODO move to separate file
data class RoleAssignmentRole
@JsonCreator constructor(
  @JvmField
  val slug: String
)

// TODO move to separate file
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
