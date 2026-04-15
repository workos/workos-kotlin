package com.workos.events.models

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

data class OrganizationEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationEventData
) : Event(id, event, createdAt, context)

data class ConnectionEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Connection
) : Event(id, event, createdAt, context)

data class DirectoryUserEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryUserMinimal
) : Event(id, event, createdAt, context)

data class DirectoryGroupEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryGroupEventData
) : Event(id, event, createdAt, context)

data class DirectoryEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryEventData
) : Event(id, event, createdAt, context)

data class UserEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: UmUser
) : Event(id, event, createdAt, context)

data class AuthenticationEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: AuthenticationEventData
) : Event(id, event, createdAt, context)

data class EmailVerificationEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: EmailVerificationEventData
) : Event(id, event, createdAt, context)

data class MagicAuthEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: MagicAuthEventData
) : Event(id, event, createdAt, context)

data class InvitationEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: InvitationEventData
) : Event(id, event, createdAt, context)

data class PasswordResetEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: PasswordResetEventData
) : Event(id, event, createdAt, context)

data class OrganizationDomainEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationDomain
) : Event(id, event, createdAt, context)

data class RoleEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: RolesRole
) : Event(id, event, createdAt, context)

data class OrganizationMembershipEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationMembership
) : Event(id, event, createdAt, context)

data class EventDirectoryUser(
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

data class OrganizationEventData(
  val id: String,
  val name: String
)

data class DirectoryEventData(
  val id: String,
  val name: String
)

data class DirectoryGroupEventData(
  val id: String,
  val name: String
)

data class DirectoryUserMinimal(
  val id: String,
  val email: String?
)

data class SessionImpersonator(
  val email: String?,
  val reason: String?
)

data class SessionEventData(
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

data class SessionEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: SessionEventData
) : Event(id, event, createdAt, context)

data class DirectoryGroupMembershipData(
  val user: EventDirectoryUser,
  val group: Group
)

data class DirectoryGroupMembershipEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: DirectoryGroupMembershipData
) : Event(id, event, createdAt, context)

data class FlagEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: FlagEventsApiData
) : Event(id, event, createdAt, context)

data class ApiKeyEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: ApiKeyEventsApiData
) : Event(id, event, createdAt, context)

data class OrganizationRoleEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: OrganizationRoleEventsApiData
) : Event(id, event, createdAt, context)

data class PermissionEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: PermissionEventsApiData
) : Event(id, event, createdAt, context)

data class FlagEventsApiData(
  val id: String,
  val slug: String,
  val name: String,
  val enabled: Boolean? = null,
  @JsonProperty("default_value") val defaultValue: Boolean? = null
)

data class ApiKeyEventsApiData(
  val id: String,
  val name: String,
  @JsonProperty("obfuscated_value") val obfuscatedValue: String? = null
)

data class OrganizationRoleEventsApiData(
  val slug: String,
  val name: String,
  @JsonProperty("organization_id") val organizationId: String? = null,
  val permissions: List<String> = emptyList()
)

data class PermissionEventsApiData(
  val id: String,
  val slug: String,
  val name: String
)

data class RadarRiskDetectedEventsApiEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: RadarRiskDetectedEventsApiData
) : Event(id, event, createdAt, context)

data class RadarRiskDetectedEventsApiData(
  @JsonProperty("auth_method") val authMethod: String,
  val action: String,
  val control: String? = null,
  @JsonProperty("blocklist_type") val blocklistType: String? = null,
  @JsonProperty("ip_address") val ipAddress: String? = null,
  @JsonProperty("user_agent") val userAgent: String? = null,
  @JsonProperty("user_id") val userId: String,
  val email: String
)

data class SamlCertificateRenewalRequiredEventsApiEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: SamlCertificateRenewalRequiredEventsApiData
) : Event(id, event, createdAt, context)

data class SamlCertificateRenewalRequiredEventsApiData(
  val connection: SamlCertificateConnectionEventsApi,
  val certificate: SamlCertificateInfoEventsApi,
  @JsonProperty("days_until_expiry") val daysUntilExpiry: Int
)

data class SamlCertificateRenewedEventsApiEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: SamlCertificateRenewedEventsApiData
) : Event(id, event, createdAt, context)

data class SamlCertificateRenewedEventsApiData(
  val connection: SamlCertificateConnectionEventsApi,
  val certificate: SamlCertificateInfoEventsApi,
  @JsonProperty("renewed_at") val renewedAt: String
)

data class SamlCertificateConnectionEventsApi(
  val id: String,
  @JsonProperty("organization_id") val organizationId: String? = null
)

data class SamlCertificateInfoEventsApi(
  @JsonProperty("certificate_type") val certificateType: String,
  @JsonProperty("expiry_date") val expiryDate: String,
  @JsonProperty("is_expired") val isExpired: Boolean? = null
)

data class UnknownEvent(
  override val id: String,
  override val event: String,
  override val createdAt: String,
  override val context: Map<String, Any>? = null,
  val data: Any?
) : Event(id, event, createdAt, context)
