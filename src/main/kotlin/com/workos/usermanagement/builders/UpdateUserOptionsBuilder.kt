package com.workos.usermanagement.builders

import com.workos.usermanagement.types.PasswordHashTypeEnumType
import com.workos.usermanagement.types.UpdateUserOptions

/**
 * Builder for options when updating a user.
 *
 * @param id The unique ID of the user.
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param emailVerified Whether the user’s email address was previously verified.
 * @param password The password to set for the user.
 * @param passwordHash The hashed password to set for the user. Mutually exclusive with password.
 * @param passwordHashType The algorithm originally used to hash the password, used when providing a password_hash.
 */
class UpdateUserOptionsBuilder @JvmOverloads constructor(
  val id: String,
  override var firstName: String? = null,
  override var lastName: String? = null,
  override var emailVerified: Boolean? = null,
  override var password: String? = null,
  override var passwordHash: String? = null,
  override var passwordHashType: PasswordHashTypeEnumType? = null,
) : AbstractUserOptionsBuilder<UpdateUserOptions>(
  password,
  passwordHash,
  passwordHashType,
  firstName,
  lastName,
  emailVerified
) {
  /**
   * Generates the UpdateUserOptions object.
   */
  override fun build(): UpdateUserOptions {
    return UpdateUserOptions(
      id = this.id,
      firstName = this.firstName,
      lastName = this.lastName,
      emailVerified = this.emailVerified,
      password = this.password,
      passwordHash = this.passwordHash,
      passwordHashType = this.passwordHashType,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(id: String): UpdateUserOptionsBuilder {
      return UpdateUserOptionsBuilder(id)
    }
  }
}
