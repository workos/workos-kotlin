package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data payload for `organization_role.*` webhook events.
 */
data class OrganizationRoleEventData
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "organization_role",

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String,

  @JvmField
  val slug: String,

  @JvmField
  val name: String,

  @JvmField
  val description: String? = null,

  @JvmField
  @JsonProperty("resource_type_slug")
  val resourceTypeSlug: String,

  @JvmField
  val permissions: List<String> = emptyList(),

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String
)
