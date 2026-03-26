package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of event types for a [WebhookEvent]
 *
 * @param value The Event Type string value.
 */
enum class EventType(
  @JsonValue @JvmField
  val value: String
) {
  /**
   * Triggers when a user fails to verify their email.
   */
  AuthenticationEmailVerificationFailed("authentication.email_verification_failed"),

  /**
   * Triggers when a user successfully verifies their email.
   */
  AuthenticationEmailVerificationSucceeded("authentication.email_verification_succeeded"),

  /**
   * Triggers when a user fails to authenticate via Magic Auth.
   */
  AuthenticationMagicAuthFailed("authentication.magic_auth_failed"),

  /**
   * Triggers when a user successfully authenticates via Magic Auth.
   */
  AuthenticationMagicAuthSucceeded("authentication.magic_auth_succeeded"),

  /**
   * Triggers when a user fails to authenticate with a multi-factor authentication code.
   */
  AuthenticationMfaFailed("authentication.mfa_failed"),

  /**
   * Triggers when a user successfully authenticates with a multi-factor authentication code.
   */
  AuthenticationMfaSucceeded("authentication.mfa_succeeded"),

  /**
   * Triggers when a user fails to authenticate via OAuth.
   */
  AuthenticationOauthFailed("authentication.oauth_failed"),

  /**
   * Triggers when a user successfully authenticates via OAuth.
   */
  AuthenticationOauthSucceeded("authentication.oauth_succeeded"),

  /**
   * Triggers when a user fails to authenticate with a passkey.
   */
  AuthenticationPasskeyFailed("authentication.passkey_failed"),

  /**
   * Triggers when a user successfully authenticates with a passkey.
   */
  AuthenticationPasskeySucceeded("authentication.passkey_succeeded"),

  /**
   * Triggers when a user fails to authenticate with password credentials.
   */
  AuthenticationPasswordFailed("authentication.password_failed"),

  /**
   * Triggers when a user successfully authenticates with password credentials.
   */
  AuthenticationPasswordSucceeded("authentication.password_succeeded"),

  /**
   * Triggers when Radar detects a risk during authentication.
   */
  AuthenticationRadarRiskDetected("authentication.radar_risk_detected"),

  /**
   * Triggers when a user fails to authenticate with Single Sign-On.
   */
  AuthenticationSsoFailed("authentication.sso_failed"),

  /**
   * Triggers when a user starts authenticating with Single Sign-On.
   */
  AuthenticationSsoStarted("authentication.sso_started"),

  /**
   * Triggers when a user successfully authenticates with Single Sign-On.
   */
  AuthenticationSsoSucceeded("authentication.sso_succeeded"),

  /**
   * Triggers when a Single Sign-On authentication attempt times out.
   */
  AuthenticationSsoTimedOut("authentication.sso_timed_out"),

  /**
   * Triggers when an API key is created.
   */
  ApiKeyCreated("api_key.created"),

  /**
   * Triggers when an API key is revoked.
   */
  ApiKeyRevoked("api_key.revoked"),

  /**
   * Triggers when a Connection's state changes to active.
   */
  ConnectionActivated("connection.activated"),

  /**
   * Triggers when a Connection's state changes to inactive.
   */
  ConnectionDeactivated("connection.deactivated"),

  /**
   * Triggers when a Connection is deleted.
   */
  ConnectionDeleted("connection.deleted"),

  /**
   * Triggers when a Connection's SAML certificate is approaching expiry.
   */
  ConnectionSamlCertificateRenewalRequired("connection.saml_certificate_renewal_required"),

  /**
   * Triggers when a Connection's SAML certificate has been renewed.
   */
  ConnectionSamlCertificateRenewed("connection.saml_certificate_renewed"),

  /**
   * Triggers when a Directory's state changes to active.
   */
  DirectoryActivated("dsync.activated"),

  /**
   * Triggers when a Directory's state changes to inactive.
   */
  DirectoryDeactivated("dsync.deactivated"),

  /**
   * Triggers when a Directory is deleted.
   */
  DirectoryDeleted("dsync.deleted"),

  /**
   * Triggers when a user is created or added to the directory
   */
  DirectoryUserCreated("dsync.user.created"),

  /**
   * Triggers when a user's properties have been updated.
   */
  DirectoryUserUpdated("dsync.user.updated"),

  /**
   * Triggers when a user has been removed from a directory.
   */
  DirectoryUserDeleted("dsync.user.deleted"),

  /**
   * Triggers when a group is created or added to a directory.
   */
  DirectoryGroupCreated("dsync.group.created"),

  /**
   * Triggers when a group's properties have been updated.
   */
  DirectoryGroupUpdated("dsync.group.updated"),

  /**
   * Triggers when a user has been added to a group.
   */
  DirectoryGroupUserAdded("dsync.group.user_added"),

  /**
   * Triggers when a user has been removed from a group.
   */
  DirectoryGroupUserRemoved("dsync.group.user_removed"),

  /**
   * Triggers when a group is removed from a directory.
   */
  DirectoryGroupDeleted("dsync.group.deleted"),

  /**
   * Triggers when a user is required to verify their email.
   */
  EmailVerificationCreated("email_verification.created"),

  /**
   * Triggers when a feature flag is created.
   */
  FlagCreated("flag.created"),

  /**
   * Triggers when a feature flag is deleted.
   */
  FlagDeleted("flag.deleted"),

  /**
   * Triggers when a feature flag's rule is updated.
   */
  FlagRuleUpdated("flag.rule_updated"),

  /**
   * Triggers when a feature flag is updated.
   */
  FlagUpdated("flag.updated"),

  /**
   * Triggers when a user accepts an invitation.
   */
  InvitationAccepted("invitation.accepted"),

  /**
   * Triggers when a user is invited to sign up or to join an organization.
   */
  InvitationCreated("invitation.created"),

  /**
   * Triggers when an invitation is resent.
   */
  InvitationResent("invitation.resent"),

  /**
   * Triggers when an invitation is revoked.
   */
  InvitationRevoked("invitation.revoked"),

  /**
   * Triggers when a user initiates Magic Auth and an authentication code is created.
   */
  MagicAuthCreated("magic_auth.created"),

  /**
   * Triggers when an organization is created.
   */
  OrganizationCreated("organization.created"),

  /**
   * Triggers when an organization is deleted.
   */
  OrganizationDeleted("organization.deleted"),

  /**
   * Triggers when an organization is updated.
   */
  OrganizationUpdated("organization.updated"),

  /**
   * Triggers when an organization domain is created.
   */
  OrganizationDomainCreated("organization_domain.created"),

  /**
   * Triggers when an organization domain is deleted.
   */
  OrganizationDomainDeleted("organization_domain.deleted"),

  /**
   * Triggers when an organization domain is updated.
   */
  OrganizationDomainUpdated("organization_domain.updated"),

  /**
   * Triggers when an organization domain verification fails.
   */
  OrganizationDomainVerificationFailed("organization_domain.verification_failed"),

  /**
   * Triggers when an organization domain is verified.
   */
  OrganizationDomainVerified("organization_domain.verified"),

  /**
   * Triggers when an organization membership is created.
   */
  OrganizationMembershipCreated("organization_membership.created"),

  /**
   * Triggers when an organization membership is deleted.
   */
  OrganizationMembershipDeleted("organization_membership.deleted"),

  /**
   * Triggers when an organization membership is updated.
   */
  OrganizationMembershipUpdated("organization_membership.updated"),

  /**
   * Triggers when an organization role is created.
   */
  OrganizationRoleCreated("organization_role.created"),

  /**
   * Triggers when an organization role is deleted.
   */
  OrganizationRoleDeleted("organization_role.deleted"),

  /**
   * Triggers when an organization role is updated.
   */
  OrganizationRoleUpdated("organization_role.updated"),

  /**
   * Triggers when a user requests to reset their password.
   */
  PasswordResetCreated("password_reset.created"),

  /**
   * Triggers when a user successfully resets their password.
   */
  PasswordResetSucceeded("password_reset.succeeded"),

  /**
   * Triggers when a permission is created.
   */
  PermissionCreated("permission.created"),

  /**
   * Triggers when a permission is deleted.
   */
  PermissionDeleted("permission.deleted"),

  /**
   * Triggers when a permission is updated.
   */
  PermissionUpdated("permission.updated"),

  /**
   * Triggers when a role is created.
   */
  RoleCreated("role.created"),

  /**
   * Triggers when a role is deleted.
   */
  RoleDeleted("role.deleted"),

  /**
   * Triggers when a role is updated.
   */
  RoleUpdated("role.updated"),

  /**
   * Triggers when a session is created.
   */
  SessionCreated("session.created"),

  /**
   * Triggers when a session is revoked.
   */
  SessionRevoked("session.revoked"),

  /**
   * Triggers when a user is created.
   */
  UserCreated("user.created"),

  /**
   * Triggers when a user is updated.
   */
  UserUpdated("user.updated"),

  /**
   * Triggers when a user is deleted.
   */
  UserDeleted("user.deleted")
}
