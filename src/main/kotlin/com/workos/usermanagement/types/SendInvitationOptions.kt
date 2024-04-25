package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

class SendInvitationOptions(
  /**
   * The email address of the recipient.
   */
  @JsonProperty("email")
  val email: String,

  /**
   * The ID of the organization that the recipient will join.
   */
  @JsonProperty("organization_id")
  val organizationId: String? = null,

  /**
   * How many days the invitations will be valid for.
   * Must be between 1 and 30 days. Defaults to 7 days if not specified.
   */
  @JsonProperty("expires_in_days")
  val expiresInDays: Int? = null,

  /**
   * The ID of the user who invites the recipient.
   * The invitation email will mention the name of this user.
   */
  @JsonProperty("inviter_user_id")
  val inviterUserId: String? = null,

  /**
   * The role that the recipient will receive when they join the organization in the invitation.
   */
  @JsonProperty("role_slug")
  val roleSlug: String? = null,
) {
  init {
    require(email.isNotBlank()) { "Email is required" }
  }
}
