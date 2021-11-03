package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Directory
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "directory",

  @JvmField
  val id: String,

  @JvmField
  val name: String,

  @JvmField
  val domain: String?,

  @JvmField
  @JsonProperty("environment_id")
  val environmentId: String?,

  @JvmField
  @JsonProperty("external_key")
  val externalKey: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  val state: DirectoryState,

  @JvmField
  val type: DirectoryType,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,
)
