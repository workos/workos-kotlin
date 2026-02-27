package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationOrganizationMembership
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "organization_membership",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("user_id")
  val userId: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,

  @JvmField
  val status: String? = null,

  @JvmField
  @JsonProperty("custom_attributes")
  val customAttributes: Map<String, Any>? = null
)
