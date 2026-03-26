package com.workos.webhooks.models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.workos.directorysync.models.Directory
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import com.workos.organizations.models.Organization
import com.workos.organizations.models.OrganizationDomain
import com.workos.roles.models.Role
import com.workos.sso.models.Connection
import com.workos.usermanagement.models.AuthenticationEventData
import com.workos.usermanagement.models.EmailVerificationEventData
import com.workos.usermanagement.models.InvitationEventData
import com.workos.usermanagement.models.MagicAuthEventData
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.usermanagement.models.PasswordResetEventData
import com.workos.usermanagement.models.User as UserManagementUser

/**
 * Custom JSON deserializer for [com.workos.webhooks.models.WebhookEvent] events.
 */
class WebhookJsonDeserializer : JsonDeserializer<WebhookEvent>() {
  private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

  init {
    mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

  /**
   * @suppress
   */
  override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): WebhookEvent {
    val rootNode = jp?.codec?.readTree<TreeNode>(jp)
    val id = mapper.readValue(rootNode?.get("id")?.traverse(), String::class.java)
    val eventType = mapper.readValue(rootNode?.get("event")?.traverse(), EventType::class.java)!!
    val data = rootNode?.get("data")
    val createdAt = mapper.readValue(rootNode?.get("created_at")?.traverse(), String::class.java)

    return when (eventType) {
      // Authentication events
      EventType.AuthenticationEmailVerificationFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationEmailVerificationSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMagicAuthFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMagicAuthSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMfaFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMfaSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationOauthFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationOauthSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationPasskeyFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationPasskeySucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationPasswordFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationPasswordSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationRadarRiskDetected -> RadarRiskDetectedEvent(id, eventType, deserializeData(data, RadarRiskDetectedEventData::class.java), createdAt)
      EventType.AuthenticationSsoFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationSsoStarted -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationSsoSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationSsoTimedOut -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)

      // API Key events
      EventType.ApiKeyCreated -> ApiKeyWebhookEvent(id, eventType, deserializeData(data, ApiKeyEventData::class.java), createdAt)
      EventType.ApiKeyRevoked -> ApiKeyWebhookEvent(id, eventType, deserializeData(data, ApiKeyEventData::class.java), createdAt)

      // Connection events
      EventType.ConnectionActivated -> ConnectionActivatedEvent(id, eventType, deserializeData(data, Connection::class.java), createdAt)
      EventType.ConnectionDeactivated -> ConnectionDeactivatedEvent(id, eventType, deserializeData(data, Connection::class.java), createdAt)
      EventType.ConnectionDeleted -> ConnectionDeletedEvent(id, eventType, deserializeData(data, Connection::class.java), createdAt)
      EventType.ConnectionSamlCertificateRenewalRequired -> ConnectionSamlCertificateRenewalRequiredEvent(id, eventType, deserializeData(data, SamlCertificateRenewalRequiredEventData::class.java), createdAt)
      EventType.ConnectionSamlCertificateRenewed -> ConnectionSamlCertificateRenewedEvent(id, eventType, deserializeData(data, SamlCertificateRenewedEventData::class.java), createdAt)

      // Directory Sync events
      EventType.DirectoryActivated -> DirectoryActivatedEvent(id, eventType, deserializeData(data, Directory::class.java), createdAt)
      EventType.DirectoryDeactivated -> DirectoryDeactivatedEvent(id, eventType, deserializeData(data, Directory::class.java), createdAt)
      EventType.DirectoryDeleted -> DirectoryDeletedEvent(id, eventType, deserializeData(data, Directory::class.java), createdAt)
      EventType.DirectoryUserCreated -> DirectoryUserCreatedEvent(id, eventType, deserializeData(data, User::class.java), createdAt)
      EventType.DirectoryUserUpdated -> DirectoryUserUpdatedEvent(id, eventType, deserializeData(data, UserUpdated::class.java), createdAt)
      EventType.DirectoryUserDeleted -> DirectoryUserDeletedEvent(id, eventType, deserializeData(data, User::class.java), createdAt)
      EventType.DirectoryGroupCreated -> DirectoryGroupCreatedEvent(id, eventType, deserializeData(data, Group::class.java), createdAt)
      EventType.DirectoryGroupUpdated -> DirectoryGroupUpdatedEvent(id, eventType, deserializeData(data, GroupUpdated::class.java), createdAt)
      EventType.DirectoryGroupDeleted -> DirectoryGroupDeletedEvent(id, eventType, deserializeData(data, Group::class.java), createdAt)
      EventType.DirectoryGroupUserAdded -> DirectoryGroupUserAddedEvent(id, eventType, deserializeData(data, DirectoryGroupUserEvent::class.java), createdAt)
      EventType.DirectoryGroupUserRemoved -> DirectoryGroupUserRemovedEvent(id, eventType, deserializeData(data, DirectoryGroupUserEvent::class.java), createdAt)

      // Email Verification events
      EventType.EmailVerificationCreated -> EmailVerificationEvent(id, eventType, deserializeData(data, EmailVerificationEventData::class.java), createdAt)

      // Flag events
      EventType.FlagCreated -> FlagWebhookEvent(id, eventType, deserializeData(data, FlagEventData::class.java), createdAt)
      EventType.FlagDeleted -> FlagWebhookEvent(id, eventType, deserializeData(data, FlagEventData::class.java), createdAt)
      EventType.FlagRuleUpdated -> FlagWebhookEvent(id, eventType, deserializeData(data, FlagEventData::class.java), createdAt)
      EventType.FlagUpdated -> FlagWebhookEvent(id, eventType, deserializeData(data, FlagEventData::class.java), createdAt)

      // Invitation events
      EventType.InvitationAccepted -> InvitationEvent(id, eventType, deserializeData(data, InvitationEventData::class.java), createdAt)
      EventType.InvitationCreated -> InvitationEvent(id, eventType, deserializeData(data, InvitationEventData::class.java), createdAt)
      EventType.InvitationResent -> InvitationEvent(id, eventType, deserializeData(data, InvitationEventData::class.java), createdAt)
      EventType.InvitationRevoked -> InvitationEvent(id, eventType, deserializeData(data, InvitationEventData::class.java), createdAt)

      // Magic Auth events
      EventType.MagicAuthCreated -> MagicAuthEvent(id, eventType, deserializeData(data, MagicAuthEventData::class.java), createdAt)

      // Organization events
      EventType.OrganizationCreated -> OrganizationWebhookEvent(id, eventType, deserializeData(data, Organization::class.java), createdAt)
      EventType.OrganizationDeleted -> OrganizationWebhookEvent(id, eventType, deserializeData(data, Organization::class.java), createdAt)
      EventType.OrganizationUpdated -> OrganizationWebhookEvent(id, eventType, deserializeData(data, Organization::class.java), createdAt)

      // Organization Domain events
      EventType.OrganizationDomainCreated -> OrganizationDomainEvent(id, eventType, deserializeData(data, OrganizationDomain::class.java), createdAt)
      EventType.OrganizationDomainDeleted -> OrganizationDomainEvent(id, eventType, deserializeData(data, OrganizationDomain::class.java), createdAt)
      EventType.OrganizationDomainUpdated -> OrganizationDomainEvent(id, eventType, deserializeData(data, OrganizationDomain::class.java), createdAt)
      EventType.OrganizationDomainVerificationFailed -> OrganizationDomainEvent(id, eventType, deserializeData(data, OrganizationDomain::class.java), createdAt)
      EventType.OrganizationDomainVerified -> OrganizationDomainEvent(id, eventType, deserializeData(data, OrganizationDomain::class.java), createdAt)

      // Organization Membership events
      EventType.OrganizationMembershipCreated -> OrganizationMembershipEvent(id, eventType, deserializeData(data, OrganizationMembership::class.java), createdAt)
      EventType.OrganizationMembershipDeleted -> OrganizationMembershipEvent(id, eventType, deserializeData(data, OrganizationMembership::class.java), createdAt)
      EventType.OrganizationMembershipUpdated -> OrganizationMembershipEvent(id, eventType, deserializeData(data, OrganizationMembership::class.java), createdAt)

      // Organization Role events
      EventType.OrganizationRoleCreated -> OrganizationRoleWebhookEvent(id, eventType, deserializeData(data, OrganizationRoleEventData::class.java), createdAt)
      EventType.OrganizationRoleDeleted -> OrganizationRoleWebhookEvent(id, eventType, deserializeData(data, OrganizationRoleEventData::class.java), createdAt)
      EventType.OrganizationRoleUpdated -> OrganizationRoleWebhookEvent(id, eventType, deserializeData(data, OrganizationRoleEventData::class.java), createdAt)

      // Password Reset events
      EventType.PasswordResetCreated -> PasswordResetEvent(id, eventType, deserializeData(data, PasswordResetEventData::class.java), createdAt)
      EventType.PasswordResetSucceeded -> PasswordResetEvent(id, eventType, deserializeData(data, PasswordResetEventData::class.java), createdAt)

      // Permission events
      EventType.PermissionCreated -> PermissionWebhookEvent(id, eventType, deserializeData(data, PermissionEventData::class.java), createdAt)
      EventType.PermissionDeleted -> PermissionWebhookEvent(id, eventType, deserializeData(data, PermissionEventData::class.java), createdAt)
      EventType.PermissionUpdated -> PermissionWebhookEvent(id, eventType, deserializeData(data, PermissionEventData::class.java), createdAt)

      // Role events
      EventType.RoleCreated -> RoleWebhookEvent(id, eventType, deserializeData(data, Role::class.java), createdAt)
      EventType.RoleDeleted -> RoleWebhookEvent(id, eventType, deserializeData(data, Role::class.java), createdAt)
      EventType.RoleUpdated -> RoleWebhookEvent(id, eventType, deserializeData(data, Role::class.java), createdAt)

      // Session events
      EventType.SessionCreated -> SessionWebhookEvent(id, eventType, deserializeData(data, SessionWebhookEventData::class.java), createdAt)
      EventType.SessionRevoked -> SessionWebhookEvent(id, eventType, deserializeData(data, SessionWebhookEventData::class.java), createdAt)

      // User events
      EventType.UserCreated -> UserCreatedEvent(id, eventType, deserializeData(data, UserManagementUser::class.java), createdAt)
      EventType.UserUpdated -> UserUpdatedEvent(id, eventType, deserializeData(data, UserManagementUser::class.java), createdAt)
      EventType.UserDeleted -> UserDeletedEvent(id, eventType, deserializeData(data, UserManagementUser::class.java), createdAt)
    }
  }

  private fun <T> deserializeData(data: TreeNode?, clazz: Class<T>): T {
    return mapper.treeToValue(data, clazz)
  }
}
