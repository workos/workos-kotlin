package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class Directory
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory",

  val id: String,

  val name: String,

  val domain: String?,

  @JsonProperty("environment_id")
  val environmentId: String?,

  @JsonProperty("external_key")
  val externalKey: String?,

  @JsonProperty("organization_id")
  val organizationId: String?,

  val state: DirectoryState,

  val type: DirectoryType,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String,
)
