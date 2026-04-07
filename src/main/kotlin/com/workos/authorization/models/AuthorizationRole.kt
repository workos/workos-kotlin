package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationRole
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("object")
  val obj: String = "role",

  @JvmField
  @JsonProperty("slug")
  val slug: String,

  @JvmField
  @JsonProperty("name")
  val name: String,

  @JvmField
  @JsonProperty("description")
  val description: String? = null,

  @JvmField
  @JsonProperty("type")
  val type: String,

  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String? = null,

  @JvmField
  @JsonProperty("permissions")
  val permissions: List<String>? = null,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
