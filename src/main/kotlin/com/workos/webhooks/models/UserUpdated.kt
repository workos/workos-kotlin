package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.Email
import com.workos.directorysync.models.GroupState

/**
 * @suppress
 */
open class UserUpdated
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "directory_user",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("directory_id")
  val directoryId: String,

  @JvmField
  @JsonProperty("organization_id")
  val organizationId: String?,

  @JvmField
  @JsonProperty("idp_id")
  val idpId: String,

  @JvmField
  @JsonProperty("username")
  val userName: String?,

  @JvmField
  @JsonProperty("first_name")
  val firstName: String?,

  @JvmField
  @JsonProperty("last_name")
  val lastName: String?,

  @JvmField
  @JsonProperty("job_title")
  val jobTitle: String?,

  @JvmField
  @JsonProperty("updated_at")
  val updatedAt: String,

  @JvmField
  @JsonProperty("created_at")
  val createdAt: String,

  @JvmField
  val emails: List<Email>,

  @JvmField
  val state: GroupState,

  @JvmField
  @JsonProperty("custom_attributes")
  val customAttributes: Map<String, Any?>,

  @JvmField
  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, Any?>,

  /**
   * Object containing the names and values of attributes and their previous values.
   * New attributes that do not appear in the previous snapshot are indicated with a
   * null value.
   */
  @JvmField
  @JsonProperty("previous_attributes")
  val previousAttributes: Map<String, Any?>
)
