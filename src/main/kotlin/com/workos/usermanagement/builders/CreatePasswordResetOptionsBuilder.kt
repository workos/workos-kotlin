package com.workos.usermanagement.builders

import com.workos.usermanagement.types.CreatePasswordResetOptions

/**
 * Builder for options when creating a password reset token.
 *
 * @param email The email address of the user.
 */
class CreatePasswordResetOptionsBuilder @JvmOverloads constructor(
  private var email: String,
) {
  /**
   * Generates the CreatePasswordResetOptions object.
   */
  fun build(): CreatePasswordResetOptions {
    return CreatePasswordResetOptions(
      email = this.email,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(email: String): CreatePasswordResetOptionsBuilder {
      return CreatePasswordResetOptionsBuilder(email)
    }
  }
}
