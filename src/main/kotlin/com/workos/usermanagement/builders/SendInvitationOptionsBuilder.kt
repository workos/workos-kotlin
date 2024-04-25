package com.workos.usermanagement.builders

import com.workos.usermanagement.types.SendInvitationOptions

/**
 * Builder for options when sending an invitation.
 *
 * @param email The email address of the user.
 * @param organizationId The ID of the organization that the recipient will join.
 * @param expiresInDays How many days the invitations will be valid for.
 * @param inviterUserId The ID of the user who invites the recipient.
 * @param roleSlug The role that the recipient will receive when they join the organization in the invitation.
 */
class SendInvitationOptionsBuilder @JvmOverloads constructor(
  private var email: String,
  private var organizationId: String? = null,
  private var expiresInDays: Int? = null,
  private var inviterUserId: String? = null,
  private var roleSlug: String? = null,
) {
  /**
   * Organization ID
   */
  fun organizationId(value: String) = apply { organizationId = value }

  /**
   * Expires In Days
   */
  fun expiresInDays(value: Int) = apply { expiresInDays = value }

  /**
   * Inviter User Id
   */
  fun inviterUserId(value: String) = apply { inviterUserId = value }

  /**
   * Role Slug
   */
  fun roleSlug(value: String) = apply { roleSlug = value }

  /**
   * Generates the SendInvitationOptions object.
   */
  fun build(): SendInvitationOptions {
    return SendInvitationOptions(
      email = this.email,
      organizationId = this.organizationId,
      expiresInDays = this.expiresInDays,
      inviterUserId = this.inviterUserId,
      roleSlug = this.roleSlug,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(email: String): SendInvitationOptionsBuilder {
      return SendInvitationOptionsBuilder(email)
    }
  }
}
