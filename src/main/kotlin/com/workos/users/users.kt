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
import com.fasterxml.jackson.databind.ObjectMapper


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
   * removes a user from a specified organization.
   */
  fun removeUserFromOrganization(removeUserFromOrganizationOptions: RemoveUserFromOrganizationOptions): User {
    val config = RequestConfig
      .builder()
      .data(removeUserFromOrganizationOptions)
      .build()


    val users = workos.delete("/users", config)

    /**
     * Uncertain about this.
     */
    val mapper = ObjectMapper()
    return mapper.readValue(users, User::class.java)

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
   * Options for listing users.
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

    companion object {
      @JvmStatic
      fun builder(): ListUsersOptionsPaginationParamsBuilder {
        return ListUsersOptionsPaginationParamsBuilder()
      }
    }

    class ListUsersOptionsPaginationParamsBuilder : PaginationParamsBuilder<ListUsersOptions>(ListUsersOptions()) {
      fun type(value: UserType) = apply { this.params["type"] = value.toString() }
      fun email(value: String) = apply { this.params["email"] = value }
      fun organization(value: String) = apply { this.params["organization"] = value }
    }
  }

  /**
   * Options for removing a user from an organization.
   */
  class RemoveUserFromOrganizationOptions(
    val id: String,
    val organization: String
  ) {
    init {
      require(id.isNotBlank()) { "User id is required" }
      require(organization.isNotBlank()) { "Organization id is required" }
    }

    companion object {
      @JvmStatic
      fun builder(): RemoveUserFromOrganizationOptionsBuilder {
        return RemoveUserFromOrganizationOptionsBuilder()
      }
    }

    class RemoveUserFromOrganizationOptionsBuilder {
      private lateinit var id: String
      private lateinit var organization: String

      fun id(value: String) = apply { this.id= value }
      fun organization(value: String) = apply { this.organization = value }

      fun build(): RemoveUserFromOrganizationOptions {
        return RemoveUserFromOrganizationOptions(id, organization)
      }
    }
  }

  /**
   * Options for creating a user.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class CreateUserOptions @JvmOverloads constructor(
    @JsonProperty("email") val email: String,
    @JsonProperty("password") val password: String? = null,
    @JsonProperty("first_name") val firstName: String? = null,
    @JsonProperty("last_name") val lastName: String? = null,
    @JsonProperty("email_verified") val emailVerified: Boolean = false
  ) {
    init {
      require(email.isNotBlank()) { "Email is required" }
    }

    companion object {
      @JvmStatic
      fun builder(): CreateUserOptionsBuilder {
        return CreateUserOptionsBuilder()
      }
    }

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
        return CreateUserOptions(email, password, firstName, lastName, emailVerified)
      }
    }
  }
}
