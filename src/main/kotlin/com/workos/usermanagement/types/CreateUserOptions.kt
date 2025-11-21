package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateUserOptions @JvmOverloads constructor(
  /**
   * The email address of the user.
   */
  @JsonProperty("email")
  val email: String,

  /**
   * The password to set for the user.
   */
  @JsonProperty("password")
  val password: String? = null,

  /**
   * The hashed password to set for the user. Mutually exclusive with password.
   */
  @JsonProperty("password_hash")
  val passwordHash: String? = null,

  /**
   * The algorithm originally used to hash the password, used when providing a password_hash.
   */
  @JsonProperty("password_hash_type")
  val passwordHashType: PasswordHashTypeEnumType? = null,

  /**
   * The first name of the user.
   */
  @JsonProperty("first_name")
  val firstName: String? = null,

  /**
   * The last name of the user.
   */
  @JsonProperty("last_name")
  val lastName: String? = null,

  /**
   * Whether the user's email address was previously verified.
   *
   * You should normally use the email verification flow to verify a user's email address. However, if the user's email was previously verified, or is being migrated from an existing user store, this can be set to true to mark it as already verified.
   */
  @JsonProperty("email_verified")
  val emailVerified: Boolean? = null,

  /**
   * The external identifier of the user.
   */
  @JsonProperty("external_id")
  val externalId: String? = null,

  /**
   * Object containing metadata key/value pairs associated with the user.
   */
  @JsonProperty("metadata")
  val metadata: Map<String, String>? = null
) {
  init {
    require(email.isNotBlank()) { "Email is required" }
  }
}
