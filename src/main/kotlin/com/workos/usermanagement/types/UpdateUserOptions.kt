package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class UpdateUserOptions @JvmOverloads constructor(
  /**
   * The unique ID of the user.
   */
  @JsonProperty("id")
  val id: String,

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
   * Whether the user’s email address was previously verified.
   */
  @JsonProperty("email_verified")
  val emailVerified: Boolean? = null,

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
  val passwordHashType: PasswordHashTypeEnumType? = null
) {
  init {
    require(id.isNotBlank()) { "User id is required" }
  }
}
