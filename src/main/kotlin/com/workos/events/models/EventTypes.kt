package com.workos.events.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.DirectoryUserRole
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.UserState
import com.workos.organizations.models.OrganizationDomain
import com.workos.sso.models.Connection
import com.workos.usermanagement.models.AuthenticationEventData
import com.workos.usermanagement.models.EmailVerificationEventData
import com.workos.usermanagement.models.InvitationEventData
import com.workos.usermanagement.models.MagicAuthEventData
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.usermanagement.models.PasswordResetEventData
import com.workos.roles.models.Role as RolesRole
import com.workos.usermanagement.models.User as UmUser

/**
 * Strongly-typed Event variants used by EventsJsonDeserializer.
 */

data class OrganizationEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationEventData
) : Event(id, event, createdAt, context)

data class ConnectionEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Connection
) : Event(id, event, createdAt, context)
data class DirectoryUserEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryUserMinimal
) : Event(id, event, createdAt, context)

data class DirectoryGroupEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryGroupEventData
) : Event(id, event, createdAt, context)

data class DirectoryEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryEventData
) : Event(id, event, createdAt, context)

data class UserEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: UmUser
) : Event(id, event, createdAt, context)

data class AuthenticationEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: AuthenticationEventData
) : Event(id, event, createdAt, context)

data class EmailVerificationEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: EmailVerificationEventData
) : Event(id, event, createdAt, context)

data class MagicAuthEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: MagicAuthEventData
) : Event(id, event, createdAt, context)

data class InvitationEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: InvitationEventData
) : Event(id, event, createdAt, context)

data class PasswordResetEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: PasswordResetEventData
) : Event(id, event, createdAt, context)

data class OrganizationDomainEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationDomain
) : Event(id, event, createdAt, context)

data class RoleEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: RolesRole
) : Event(id, event, createdAt, context)

data class OrganizationMembershipEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationMembership
) : Event(id, event, createdAt, context)

data class EventDirectoryUser
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
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

data class OrganizationEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val name: String
)

data class DirectoryEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val name: String
)

data class DirectoryGroupEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val name: String
)

data class DirectoryUserMinimal
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val email: String?
)

data class SessionImpersonator
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val email: String?,
  val reason: String?
)

data class SessionEventData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val obj: String = "session",
  val id: String,
  @JsonProperty("user_id") val userId: String?,
  @JsonProperty("organization_id") val organizationId: String?,
  val impersonator: SessionImpersonator?,
  @JsonProperty("ip_address") val ipAddress: String?,
  @JsonProperty("user_agent") val userAgent: String?,
  @JsonProperty("created_at") val createdAt: String,
  @JsonProperty("updated_at") val updatedAt: String
)

data class SessionEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: SessionEventData
) : Event(id, event, createdAt, context)

data class DirectoryGroupMembershipData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val user: EventDirectoryUser,
  val group: Group
)

data class DirectoryGroupMembershipEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryGroupMembershipData
) : Event(id, event, createdAt, context)

data class FlagEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: FlagEventsApiData
) : Event(id, event, createdAt, context)

data class ApiKeyEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: ApiKeyEventsApiData
) : Event(id, event, createdAt, context)

data class OrganizationRoleEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationRoleEventsApiData
) : Event(id, event, createdAt, context)

data class PermissionEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: PermissionEventsApiData
) : Event(id, event, createdAt, context)

data class FlagEventsApiData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val slug: String,
  val name: String,
  val enabled: Boolean? = null,
  @JsonProperty("default_value") val defaultValue: Boolean? = null
)

data class ApiKeyEventsApiData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val name: String,
  @JsonProperty("obfuscated_value") val obfuscatedValue: String? = null
)

data class OrganizationRoleEventsApiData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val slug: String,
  val name: String,
  @JsonProperty("organization_id") val organizationId: String? = null,
  val permissions: List<String> = emptyList()
)

data class PermissionEventsApiData
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  val id: String,
  val slug: String,
  val name: String
)

data class UnknownEvent
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Any?
) : Event(id, event, createdAt, context)
