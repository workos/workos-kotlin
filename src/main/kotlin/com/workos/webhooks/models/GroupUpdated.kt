package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.Group

/**
 * @suppress
 */
class GroupUpdated(
  @JsonProperty("object")
  override val obj: String = "directory_group",
  @JsonProperty("directory_id")
  override val directoryId: String,
  @JsonProperty("organization_id")
  override val organizationId: String?,
  override val id: String,
  @JsonProperty("idp_id")
  override val idpId: String,
  override val name: String,
  @JsonProperty("updated_at")
  override val updatedAt: String,
  @JsonProperty("created_at")
  override val createdAt: String,
  @JsonProperty("raw_attributes")
  override val rawAttributes: Map<String, Any?>,
  /**
   * Object containing the names and values of attributes and their previous values.
   * New attributes that do not appear in the previous snapshot are indicated with a
   * null value.
   */
  @JvmField
  @JsonProperty("previous_attributes")
  val previousAttributes: Map<String, Any?>
) : Group(obj, directoryId, organizationId, id, idpId, name, createdAt, updatedAt, rawAttributes)
