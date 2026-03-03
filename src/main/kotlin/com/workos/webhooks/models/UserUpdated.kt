package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.DirectoryUserRole
import com.workos.directorysync.models.Email
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import com.workos.directorysync.models.UserState

/**
 * @suppress
 */
open class UserUpdated
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  override val obj: String = "directory_user",

  @JvmField
  override val id: String,

  @JvmField
  @JsonProperty("directory_id")
  override val directoryId: String,

  @JvmField
  @JsonProperty("organization_id")
  override val organizationId: String?,

  @JvmField
  @JsonProperty("idp_id")
  override val idpId: String,

  @JvmField
  override val email: String?,

  @Deprecated(
    "Will be removed in a future major version. Enable the `username` custom attribute in dashboard and pull from customAttributes instead. See https://workos.com/docs/directory-sync/attributes/custom-attributes/auto-mapped-attributes for details.",
    ReplaceWith("customAttributes[\"username\"]")
  )
  @JvmField
  @JsonProperty("username")
  override val userName: String?,

  @JvmField
  @JsonProperty("first_name")
  override val firstName: String?,

  @JvmField
  @JsonProperty("last_name")
  override val lastName: String?,

  @Deprecated(
    "Will be removed in a future major version. Enable the `job_title` custom attribute in dashboard and pull from customAttributes instead. See https://workos.com/docs/directory-sync/attributes/custom-attributes/auto-mapped-attributes for details.",
    ReplaceWith("customAttributes[\"job_title\"]")
  )
  @JvmField
  @JsonProperty("job_title")
  override val jobTitle: String?,

  @JvmField
  @JsonProperty("created_at")
  override val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  override val updatedAt: String,

  @Deprecated(
    "Will be removed in a future major version. Enable the `emails` custom attribute in dashboard and pull from customAttributes instead. See https://workos.com/docs/directory-sync/attributes/custom-attributes/auto-mapped-attributes for details.",
    ReplaceWith("customAttributes[\"emails\"]")
  )
  @JvmField
  override val emails: List<Email>,

  @JvmField
  override val groups: List<Group>? = null,

  @JvmField
  override val state: UserState,

  @JvmField
  @JsonProperty("role")
  override val role: DirectoryUserRole? = null,

  @JvmField
  @JsonProperty("roles")
  override val roles: List<DirectoryUserRole>? = null,

  @JvmField
  @JsonProperty("custom_attributes")
  override val customAttributes: Map<String, Any?>,

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
) : User(obj, id, directoryId, organizationId, idpId, email, userName, firstName, lastName,
         jobTitle, createdAt, updatedAt, emails, groups, state, role, roles, customAttributes, rawAttributes)
