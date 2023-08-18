package com.workos.users

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.models.Order
import com.workos.common.http.RequestConfig
import com.workos.users.models.User
import com.workos.users.models.UserType
import com.workos.users.models.UserList
import com.workos.users.models.VerifySessionResponse
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
   * Adds a user to a specified organization.
   */
  fun addUserToOrganization(addUserToOrganizationOptions: AddUserToOrganizationOptions): User {
    val id = addUserToOrganizationOptions.id
    val organization = addUserToOrganizationOptions.organization

    return workos.post("/users/$id/organizations/$organization",  User::class.java)

  }

  /**
   * Removes a user from a specified organization.
   */
  fun removeUserFromOrganization(removeUserFromOrganizationOptions: RemoveUserFromOrganizationOptions): User {
    val id = removeUserFromOrganizationOptions.id
    val organization = removeUserFromOrganizationOptions.organization

    val users = workos.delete("/users/$id/organizations/$organization")

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
   * Adds a user to a specified organization.
   */
  fun verifySession(verifySessionOptions: VerifySessionOptions): VerifySessionResponse {
    val config = RequestConfig
      .builder()
      .data(verifySessionOptions)
      .build()

    return workos.post(
      "/users/sessions/verify",
      VerifySessionResponse::class.java,
      config
    )
  }

  /**
   * Resets user password using token that was sent to the user.
   */
  fun completePasswordReset(completePasswordResetOptions: CompletePasswordResetOptions): User {
    val config = RequestConfig
      .builder()
      .data(completePasswordResetOptions)
      .build()

    return workos.post(
      "/users/password_reset",
      User::class.java,
      config
    )
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
   * Options for adding a user to an organization.
   */
  class AddUserToOrganizationOptions(
    val id: String,
    val organization: String
  ) {
    init {
      require(id.isNotBlank()) { "User id is required" }
      require(organization.isNotBlank()) { "Organization id is required" }
    }

    companion object {
      @JvmStatic
      fun builder(): AddUserToOrganizationOptionsBuilder {
        return AddUserToOrganizationOptionsBuilder()
      }
    }

    class AddUserToOrganizationOptionsBuilder {
      private lateinit var id: String
      private lateinit var organization: String

      fun id(value: String) = apply { this.id= value }
      fun organization(value: String) = apply { this.organization = value }

      fun build(): AddUserToOrganizationOptions {
        return AddUserToOrganizationOptions(id, organization)
      }
    }
  }


  /**
   * Options for verifying a session.
   */
  class VerifySessionOptions(
    val token: String,
    val clientID: String
  ) {
    init {
      require(token.isNotBlank()) { "Token is required." }
      require(clientID.isNotBlank()) { "ClientID is required." }
    }

    companion object {
      @JvmStatic
      fun builder(): VerifySessionOptionsBuilder {
        return VerifySessionOptionsBuilder()
      }
    }

    class VerifySessionOptionsBuilder {
      private var token: String = ""
      private var clientID: String = ""

      fun token(value: String) = apply { this.token= value }
      fun clientID(value: String) = apply { this.clientID = value }

      fun build(): VerifySessionOptions {
        return VerifySessionOptions(token, clientID)
      }
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

  /**
   * Options for completing a password reset.
   */
  class CompletePasswordResetOptions(
    val token: String,
    val newPassword: String
  ) {
    init {
      require(token.isNotBlank()) { "Token is required" }
      require(newPassword.isNotBlank()) { "New Password id is required" }
    }

    companion object {
      @JvmStatic
      fun builder(): CompletePasswordResetOptionsBuilder {
        return CompletePasswordResetOptionsBuilder()
      }
    }

    class CompletePasswordResetOptionsBuilder {
      private lateinit var token: String
      private lateinit var newPassword: String

      fun token(value: String) = apply { this.token = value }
      fun newPassword(value: String) = apply { this.newPassword = value }

      fun build(): CompletePasswordResetOptions {
        return CompletePasswordResetOptions(token, newPassword)
      }
    }
  }
}
