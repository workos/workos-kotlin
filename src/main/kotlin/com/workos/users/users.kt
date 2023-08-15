package com.workos.users

import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.users.models.User
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


class UsersApi(private val workos: WorkOS) {
  /**
   * Creates a User.
   */
  fun createUser(createUserOptions: CreateUserOptions): User {
    val config = RequestConfig
      .builder()
      .data(createUserOptions)
      .build()

    return workos.post(
      "/users",
      User::class.java,
      config
    )
  }

  /**
   * Parameters for creating a user.
   *
   * @param email The email of the user.
   * @param password The password of the user.
   * @param firstName The first name of the user.
   * @param lastName The last name of the user.
   * @param emailVerified The status indicating if the user's email is verified.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class CreateUserOptions @JvmOverloads constructor(
    @JsonProperty("email")
    val email: String,

    @JsonProperty("password")
    val password: String? = null,

    @JsonProperty("first_name")
    val firstName: String? = null,

    @JsonProperty("last_name")
    val lastName: String? = null,

    @JsonProperty("email_verified")
    val emailVerified: Boolean = false
  ) {
    init {
      if (email.isBlank()) {
        throw IllegalArgumentException("email is required")
      }
    }

    companion object {
      @JvmStatic
      fun builder(): CreateUserOptionsBuilder {
        return CreateUserOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [CreateUserOptions].
     */
    class CreateUserOptionsBuilder {
      private lateinit var email: String
      private var password: String? = null
      private var firstName: String? = null
      private var lastName: String? = null
      private var emailVerified: Boolean = false

      fun email(value: String) = apply { this.email = value }

      fun password(value: String) = apply { this.password = value }

      fun firstName(value: String) = apply { this.firstName = value }

      fun lastName(value: String) = apply { this.lastName = value }

      fun emailVerified(value: Boolean) = apply { this.emailVerified = value }

      fun build(): CreateUserOptions {
        return CreateUserOptions(
          email = email,
          password = password,
          firstName = firstName,
          lastName = lastName,
          emailVerified = emailVerified
        )
      }
    }
  }
}
