package com.workos.users

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.models.Order
import com.workos.common.http.RequestConfig
import com.workos.users.models.User
import com.workos.users.models.UserType
import com.workos.users.models.UserList
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.sso.models.Connection


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
   * Fetches a single user by id.
   */
  fun getUser(id: String): User {
    return workos.get("/users/$id", User::class.java)
  }

  /**
   * Lists Users.
   */
  @JvmOverloads
  fun listUsers(options: ListUsersOptions? = null): UserList {
    val config = RequestConfig.builder()
      .params(options ?: ListUsersOptions())
      .build()

    return workos.get("/users", UserList::class.java, config)
}

  /**
   * Parameters for the listUsers method.
   */
  class ListUsersOptions @JvmOverloads constructor(
    type: UserType? = null,
    email: String? = null,
    organization: String? = null,
    limit: Int? = null,
    order: Order? = null,
    before: String? = null,
    after: String? = null
  ) : PaginationParams(after, before, limit, order) {
    init {
      if (type != null) set("type", type.toString())
      if (email != null) set("email", email)
      if (organization != null) set("organization", organization)
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListUsersOptionsPaginationParamsBuilder {
        return ListUsersOptionsPaginationParamsBuilder()
      }
    }

    /**
     * Parameters builder for listUsers method.
     */
    class ListUsersOptionsPaginationParamsBuilder : PaginationParamsBuilder<ListUsersOptions>(ListUsersOptions()) {
      /**
       * The UserType to filter on.
       */
      fun type(value: UserType) = apply { this.params["type"] = value.toString() }

      /**
       * The email to filter on.
       */
      fun email(value: String) = apply { this.params["email"] = value }

      /**
       * The organization to filter on.
       */
      fun organization(value: String) = apply { this.params["organization"] = value }
    }
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
