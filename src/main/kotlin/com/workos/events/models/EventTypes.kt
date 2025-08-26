package com.workos.events.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.Directory
import com.workos.directorysync.models.DirectoryUserRole
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.UserState
import com.workos.organizations.models.Organization
import com.workos.sso.models.Connection
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.directorysync.models.User as DirectoryUser
import com.workos.usermanagement.models.User as UmUser

/**
 * Strongly-typed Event variants used by EventsJsonDeserializer.
 */

data class OrganizationEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Organization
) : Event(id, event, createdAt, context)
//

data class ConnectionEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Connection
) : Event(id, event, createdAt, context)
data class DirectoryUserEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryUser
) : Event(id, event, createdAt, context)

data class DirectoryGroupEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Group
) : Event(id, event, createdAt, context)

data class DirectoryEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Directory
) : Event(id, event, createdAt, context)

data class UserEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: UmUser
) : Event(id, event, createdAt, context)

data class OrganizationMembershipEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationMembership
) : Event(id, event, createdAt, context)

data class EventDirectoryUser
@JsonCreator constructor(
  @JsonProperty("id") val id: String,
  @JsonProperty("directory_id") val directoryId: String,
  @JsonProperty("organization_id") val organizationId: String?,
  @JsonProperty("idp_id") val idpId: String,
  @JsonProperty("first_name") val firstName: String?,
  @JsonProperty("last_name") val lastName: String?,
  val email: String?,
  val state: UserState,
  @JsonProperty("created_at") val createdAt: String,
  @JsonProperty("updated_at") val updatedAt: String,
  @JsonProperty("custom_attributes") val customAttributes: Map<String, Any?>,
  val role: DirectoryUserRole?,
  @JsonProperty("raw_attributes") val rawAttributes: Map<String, Any?>
)

data class DirectoryGroupMembershipData
@JsonCreator constructor(
  val user: EventDirectoryUser,
  val group: Group
)

data class DirectoryGroupMembershipEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryGroupMembershipData
) : Event(id, event, createdAt, context)

data class UnknownEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Any?
) : Event(id, event, createdAt, context)
