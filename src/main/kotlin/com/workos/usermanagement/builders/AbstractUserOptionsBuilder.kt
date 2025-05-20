package com.workos.usermanagement.builders

import com.workos.usermanagement.types.PasswordHashTypeEnumType

/**
 * Abstract builder for options when managing users.
 *
 * @param email The email address of the user.
 * @param password The password to set for the user.
 * @param passwordHash The hashed password to set for the user. Mutually exclusive with password.
 * @param passwordHashType The algorithm originally used to hash the password, used when providing a password_hash.
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param emailVerified Whether the user’s email address was previously verified.
 */
abstract class AbstractUserOptionsBuilder<OptionsObject> @JvmOverloads constructor(
  open var password: String? = null,
  open var passwordHash: String? = null,
  open var passwordHashType: PasswordHashTypeEnumType? = null,
  open var firstName: String? = null,
  open var lastName: String? = null,
  open var emailVerified: Boolean? = null,
) {
  /**
   * Password
   */
  fun password(value: String) = apply { password = value }

  /**
   * Password Hash
   */
  fun passwordHash(value: String) = apply { passwordHash = value }

  /**
   * Password Hash Type
   */
  fun passwordHashType(value: PasswordHashTypeEnumType) = apply { passwordHashType = value }

  /**
   * First Name
   */
  fun firstName(value: String) = apply { firstName = value }

  /**
   * Last Name
   */
  fun lastName(value: String) = apply { lastName = value }

  /**
   * Email Verified
   *
   * You should normally use the [email verification flow](https://workos.com/docs/reference/user-management/authentication/email-verification) to verify a user’s email address.
   * However, if the user’s email was previously verified, or is being migrated from an
   * existing user store, this can be set to true to mark it as already verified.
   */
  fun emailVerified(value: Boolean) = apply { emailVerified = value }

  /**
   * Generates the options object.
   */
  abstract fun build(): OptionsObject
}
