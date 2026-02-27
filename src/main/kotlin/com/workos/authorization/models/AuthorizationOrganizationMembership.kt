package com.workos.authorization.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.BaseOrganizationMembership

data class AuthorizationOrganizationMembership
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "organization_membership",

  @JvmField
  @JsonProperty("id")
  override val id: String,

  @JvmField
  @JsonProperty("user_id")
  override val userId: String,

  @JvmField
  @JsonProperty("organization_id")
  override val organizationId: String,

  @JvmField
  @JsonProperty("created_at")
  override val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  override val updatedAt: String,

  @JvmField
  val status: String? = null,

  @JvmField
  @JsonProperty("custom_attributes")
  val customAttributes: Map<String, Any>? = null
) : BaseOrganizationMembership
