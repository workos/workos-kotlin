package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a Directory User resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param directoryId The unique identifier for the [Directory] the User belongs to.
 * @param organizationId The identifier for the Organization in which the Directory resides.
 * @param id The unique identifier for the Directory User
 * @param idpId Unique identifier for the user, assigned by the Directory Provider. Different Directory Providers use different ID formats. One possible use case for idp_id is associating the Directory User with their SSO Profile.
 * @param email The primary email of the user.
 * @param userName The username of the user.
 * @param firstName The first name of the user.
 * @param lastName The last name for the user.
 * @param jobTitle The job title for the user.
 * @param emails The emails of the user.
 * @param groups The groups that the user is a member of.
 * @param state The state of the user.
 * @param role The user's role based on group memberships.
 * @param customAttributes An object containing the custom attribute mapping for the Directory Provider.
 * @param rawAttributes An object containing the data returned from the Directory Provider.
 */
open class User
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  open val obj: String = "directory_user",

  @JvmField
  open val id: String,

  @JvmField
  @JsonProperty("directory_id")
  open val directoryId: String,

  @JvmField
  @JsonProperty("organization_id")
  open val organizationId: String?,

  @JvmField
  @JsonProperty("idp_id")
  open val idpId: String,

  @Deprecated(
    "Will be removed in a future major version. Enable the `username` custom attribute in dashboard and pull from customAttributes instead. See https://workos.com/docs/directory-sync/attributes/custom-attributes/auto-mapped-attributes for details.",
    ReplaceWith("customAttributes[\"username\"]")
  )
  @JvmField
  @JsonProperty("username")
  open val userName: String?,

  @JvmField
  @JsonProperty("first_name")
  open val firstName: String?,

  @JvmField
  @JsonProperty("last_name")
  open val lastName: String?,

  @Deprecated(
    "Will be removed in a future major version. Enable the `job_title` custom attribute in dashboard and pull from customAttributes instead. See https://workos.com/docs/directory-sync/attributes/custom-attributes/auto-mapped-attributes for details.",
    ReplaceWith("customAttributes[\"job_title\"]")
  )
  @JvmField
  @JsonProperty("job_title")
  open val jobTitle: String?,

  @JvmField
  @JsonProperty("created_at")
  open val createdAt: String,

  @JvmField
  @JsonProperty("updated_at")
  open val updatedAt: String,

  @Deprecated(
    "Will be removed in a future major version. Enable the `emails` custom attribute in dashboard and pull from customAttributes instead. See https://workos.com/docs/directory-sync/attributes/custom-attributes/auto-mapped-attributes for details.",
    ReplaceWith("customAttributes[\"emails\"]")
  )
  @JvmField
  open val emails: List<Email>,

  @JvmField
  open val groups: List<Group>?,

  @JvmField
  open val state: UserState,

  @JvmField
  @JsonProperty("role")
  val role: DirectoryUserRole?,

  @JvmField
  @JsonProperty("custom_attributes")
  open val customAttributes: Map<String, Any?>,

  @JvmField
  @JsonProperty("raw_attributes")
  open val rawAttributes: Map<String, Any?>
) {
  @Deprecated("Will be removed in a future major version.", ReplaceWith("email"))
  open fun primaryEmail(): String? {
    return emails.first { it.primary == true }.value
  }
}
