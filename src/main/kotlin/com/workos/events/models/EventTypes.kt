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
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationEventData
) : Event(id, event, createdAt, context)

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
  val data: DirectoryUserMinimal
) : Event(id, event, createdAt, context)

data class DirectoryGroupEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryGroupEventData
) : Event(id, event, createdAt, context)

data class DirectoryEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryEventData
) : Event(id, event, createdAt, context)

data class UserEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: UmUser
) : Event(id, event, createdAt, context)

data class AuthenticationEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: AuthenticationEventData
) : Event(id, event, createdAt, context)

data class EmailVerificationEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: EmailVerificationEventData
) : Event(id, event, createdAt, context)

data class MagicAuthEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: MagicAuthEventData
) : Event(id, event, createdAt, context)

data class InvitationEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: InvitationEventData
) : Event(id, event, createdAt, context)

data class PasswordResetEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: PasswordResetEventData
) : Event(id, event, createdAt, context)

data class OrganizationDomainEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationDomain
) : Event(id, event, createdAt, context)

data class RoleEvent
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: RolesRole
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

data class OrganizationEventData
@JsonCreator constructor(
  val id: String,
  val name: String
)

data class DirectoryEventData
@JsonCreator constructor(
  val id: String,
  val name: String
)

data class DirectoryGroupEventData
@JsonCreator constructor(
  val id: String,
  val name: String
)

data class DirectoryUserMinimal
@JsonCreator constructor(
  val id: String,
  val email: String?
)

data class SessionImpersonator
@JsonCreator constructor(
  val email: String?,
  val reason: String?
)

data class SessionEventData
@JsonCreator constructor(
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
@JsonCreator constructor(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: SessionEventData
) : Event(id, event, createdAt, context)

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
