package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RoleAssignmentRole
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("slug")
  val slug: String
)

data class RoleAssignmentResource
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("external_id")
  val externalId: String? = null,

  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String? = null
)

data class RoleAssignment
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("object")
  val obj: String = "role_assignment",

  @JvmField
  @JsonProperty("role")
  val role: RoleAssignmentRole,

  @JvmField
  @JsonProperty("resource")
  val resource: RoleAssignmentResource? = null,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
