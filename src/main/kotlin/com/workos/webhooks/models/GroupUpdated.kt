package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.Group

/**
 * @suppress
 */
class GroupUpdated
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  override val obj: String = "directory_group",

  @JvmField
  @JsonProperty("directory_id")
  override val directoryId: String,

  @JvmField
  @JsonProperty("organization_id")
  override val organizationId: String?,

  @JvmField
  override val id: String,

  @JvmField
  @JsonProperty("idp_id")
  override val idpId: String,

  @JvmField
  override val name: String,

  @JvmField
  @JsonProperty("updated_at")
  override val updatedAt: String,

  @JvmField
  @JsonProperty("created_at")
  override val createdAt: String,

  @JvmField
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
