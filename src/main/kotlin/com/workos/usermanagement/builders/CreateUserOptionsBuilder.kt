package com.workos.usermanagement.builders

import com.workos.usermanagement.types.CreateUserOptions
import com.workos.usermanagement.types.PasswordHashTypeEnumType

/**
 * Builder for options when creating a user.
 *
 * @param email The email address of the user.
 * @param password The password to set for the user.
 * @param passwordHash The hashed password to set for the user. Mutually exclusive with password.
 * @param passwordHashType The algorithm originally used to hash the password, used when providing a password_hash.
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param emailVerified Whether the user’s email address was previously verified.
 * @param externalId The external ID of the user.
 * @param metadata Object containing metadata key/value pairs associated with the user.
 */
class CreateUserOptionsBuilder @JvmOverloads constructor(
  val email: String,
  override var password: String? = null,
  override var passwordHash: String? = null,
  override var passwordHashType: PasswordHashTypeEnumType? = null,
  override var firstName: String? = null,
  override var lastName: String? = null,
  override var emailVerified: Boolean? = null,
  override var externalId: String? = null,
  override var metadata: Map<String, String>? = null,
) : AbstractUserOptionsBuilder<CreateUserOptions>(
  password,
  passwordHash,
  passwordHashType,
  firstName,
  lastName,
  emailVerified,
  externalId,
  metadata
) {
  /**
   * Generates the CreateUserOptions object.
   */
  override fun build(): CreateUserOptions {
    return CreateUserOptions(
      email = this.email,
      password = this.password,
      passwordHash = this.passwordHash,
      passwordHashType = this.passwordHashType,
      firstName = this.firstName,
      lastName = this.lastName,
      emailVerified = this.emailVerified,
      externalId = this.externalId,
      metadata = this.metadata
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(email: String): CreateUserOptionsBuilder {
      return CreateUserOptionsBuilder(email)
    }
  }
}
