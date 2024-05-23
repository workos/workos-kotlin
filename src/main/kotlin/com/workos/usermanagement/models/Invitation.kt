package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.usermanagement.types.InvitationStateEnumType

/**
 * An email invitation allows the recipient to sign up for your app and join a
 * specific organization. When an invitation is accepted, a user and a
 * corresponding organization membership are created.
 *
 * @param id The unique ID of the invitation.
 * @param email The email address of the user.
 * @param state The state of the invitation (see enum values in [InvitationStateEnumType]).
 * @param acceptedAt The timestamp when the invitation was accepted.
 * @param revokedAt The timestamp when the invitation was revoked.
 * @param expiresAt The timestamp when the invitation will expire.
 * @param token The token of an invitation.
 * @param acceptInvitationUrl The URL where the user can accept the invitation.
 * @param organizationId The ID of the organization.
 * @param inviterUserId The ID of the user that invited the recipient, if provided.
 * @param createdAt The timestamp when the invitation was created.
 * @param updatedAt The timestamp when the invitation was last updated.
 */
data class Invitation @JsonCreator constructor(
  @JsonProperty("id")
  val id: String,

  @JsonProperty("email")
  val email: String,

  @JsonProperty("state")
  val state: InvitationStateEnumType,

  @JsonProperty("accepted_at")
  val acceptedAt: String? = null,

  @JsonProperty("revoked_at")
  val revokedAt: String? = null,

  @JsonProperty("expires_at")
  val expiresAt: String,

  @JsonProperty("token")
  val token: String,

  @JsonProperty("accept_invitation_url")
  val acceptInvitationUrl: String,

  @JsonProperty("organization_id")
  val organizationId: String? = null,

  @JsonProperty("inviter_user_id")
  val inviterUserId: String? = null,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String
)
