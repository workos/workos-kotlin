package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationOrganizationMembership
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  @JvmField
  @JsonProperty("id")
  val id: String,

  @JvmField
  @JsonProperty("object")
  val obj: String = "organization_membership",

  @JvmField
  @JsonProperty("user_id")
  val userId: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String,

  @JvmField
  @JsonProperty("status")
  val status: String,

  @JvmField
  @JsonProperty("directory_managed")
  val directoryManaged: Boolean,

  @JvmField
  @JsonProperty("organization_name")
  val organizationName: String? = null,

  @JvmField
  @JsonProperty("custom_attributes")
  val customAttributes: Map<String, Any>? = null,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
