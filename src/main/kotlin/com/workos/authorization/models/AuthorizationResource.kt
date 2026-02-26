package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationResource
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "authorization_resource",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("external_id")
  val externalId: String,

  @JvmField
  val name: String,

  @JvmField
  val description: String? = null,

  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String,

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
