package com.workos.usermanagement.builders

import com.workos.usermanagement.types.CreateMagicAuthOptions

/**
 * Builder for options when creating a Magic Auth code.
 *
 * @param email The email address of the user.
 * @param invitationToken The token of an invitation, if required.
 */
class CreateMagicAuthOptionsBuilder @JvmOverloads constructor(
  private var email: String,
  private var invitationToken: String? = null,
) {
  /**
   * Invitation Token
   */
  fun invitationToken(value: String) = apply { invitationToken = value }

  /**
   * Generates the CreateMagicAuthOptions object.
   */
  fun build(): CreateMagicAuthOptions {
    return CreateMagicAuthOptions(
      email = this.email,
      invitationToken = this.invitationToken,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(email: String): CreateMagicAuthOptionsBuilder {
      return CreateMagicAuthOptionsBuilder(email)
    }
  }
}
