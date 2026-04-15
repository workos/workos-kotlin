package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationPermission(
  @JvmField
  @JsonProperty("id")
  val id: String,
  @JvmField
  @JsonProperty("object")
  val obj: String = "permission",
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
  @JsonProperty("system")
  val system: Boolean,
  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String? = null,
  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,
  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
