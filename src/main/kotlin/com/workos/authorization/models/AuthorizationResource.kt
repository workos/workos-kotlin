package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationResource(
  @JvmField
  @JsonProperty("id")
  val id: String,
  @JvmField
  @JsonProperty("object")
  val obj: String = "authorization_resource",
  @JvmField
  @JsonProperty("external_id")
  val externalId: String,
  @JvmField
  @JsonProperty("name")
  val name: String,
  @JvmField
  @JsonProperty("description")
  val description: String? = null,
  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String,
  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String,
  @JvmField
  @JsonProperty("parent_resource_id")
  val parentResourceId: String? = null,
  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,
  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
