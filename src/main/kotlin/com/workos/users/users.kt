package com.workos.users

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.models.Order
import com.workos.common.http.RequestConfig
import com.workos.users.models.AuthenticationResponse
import com.workos.users.models.User
import com.workos.users.models.UserType
import com.workos.users.models.UserList
import com.workos.users.models.VerifySessionResponse
import com.workos.users.models.CreatePasswordResetChallengeResponse
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper


class UsersApi(private val workos: WorkOS) {
  /**
   * Parameters for the [createUser] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class CreateUserOptions @JvmOverloads constructor(
    @JsonProperty("email") val email: String,
    @JsonProperty("password") val password: String? = null,
    @JsonProperty("first_name") val firstName: String? = null,
    @JsonProperty("last_name") val lastName: String? = null,
    @JsonProperty("email_verified") val emailVerified: Boolean = false
  ) {
    init {
      require(email.isNotBlank()) { "Email is required" }
    }

    /**
     * Builder class for [CreateUserOptions].
     */
    class CreateUserOptionsBuilder {
      private lateinit var email: String
      private var password: String? = null
      private var firstName: String? = null
      private var lastName: String? = null
      private var emailVerified: Boolean = false

      /**
       * Sets the email.
       */
      fun email(value: String) = apply { this.email = value }

      /**
       * Sets the password.
       */
      fun password(value: String) = apply { this.password = value }

      /**
       * Sets the first name.
       */
      fun firstName(value: String) = apply { this.firstName = value }

      /**
       * Sets the last name.
       */
      fun lastName(value: String) = apply { this.lastName = value }

      /**
       * Sets the email verified status.
       */
      fun emailVerified(value: Boolean) = apply { this.emailVerified = value }

      /**
       * Creates a [CreateUserOptions] with the given builder parameters.
       */
      fun build(): CreateUserOptions {
        return CreateUserOptions(email, password, firstName, lastName, emailVerified)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CreateUserOptionsBuilder {
        return CreateUserOptionsBuilder()
      }
    }
  }

  /**
   * Creates a User.
   */
  fun createUser(createUserOptions: CreateUserOptions): User {
    val config = RequestConfig.builder().data(createUserOptions).build()
    return workos.post("/users", User::class.java, config)
  }
  /**
   * Fetches a single user by id.
   */
  fun getUser(id: String): User {
    return workos.get("/users/$id", User::class.java)
  }

  /**
   * Parameters for the [addUserToOrganization] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class AddUserToOrganizationOptions @JvmOverloads constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("organization") val organization: String
  ) {
    init {
      require(id.isNotBlank()) { "User id is required" }
      require(organization.isNotBlank()) { "Organization id is required" }
    }

    /**
     * Builder class for [AddUserToOrganizationOptions].
     */
    class AddUserToOrganizationOptionsBuilder {
      private lateinit var id: String
      private lateinit var organization: String

      /**
       * Sets the user id.
       */
      fun id(value: String) = apply { this.id = value }

      /**
       * Sets the organization id.
       */
      fun organization(value: String) = apply { this.organization = value }

      /**
       * Creates a [AddUserToOrganizationOptions] with the given builder parameters.
       */
      fun build(): AddUserToOrganizationOptions {
        return AddUserToOrganizationOptions(id, organization)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): AddUserToOrganizationOptionsBuilder {
        return AddUserToOrganizationOptionsBuilder()
      }
    }
  }

  /**
   * Adds a user to a specified organization.
   */
  fun addUserToOrganization(addUserToOrganizationOptions: AddUserToOrganizationOptions): User {
    val id = addUserToOrganizationOptions.id
    val organization = addUserToOrganizationOptions.organization
    return workos.post("/users/$id/organizations/$organization", User::class.java)
  }

  /**
   * Parameters for the [removeUserFromOrganization] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class RemoveUserFromOrganizationOptions @JvmOverloads constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("organization") val organization: String
  ) {
    init {
      require(id.isNotBlank()) { "User id is required" }
      require(organization.isNotBlank()) { "Organization id is required" }
    }

    /**
     * Builder class for [RemoveUserFromOrganizationOptions].
     */
    class RemoveUserFromOrganizationOptionsBuilder {
      private lateinit var id: String
      private lateinit var organization: String

      /**
       * Sets the user id.
       */
      fun id(value: String) = apply { this.id = value }

      /**
       * Sets the organization id.
       */
      fun organization(value: String) = apply { this.organization = value }

      /**
       * Creates a [RemoveUserFromOrganizationOptions] with the given builder parameters.
       */
      fun build(): RemoveUserFromOrganizationOptions {
        return RemoveUserFromOrganizationOptions(id, organization)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): RemoveUserFromOrganizationOptionsBuilder {
        return RemoveUserFromOrganizationOptionsBuilder()
      }
    }
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
  fun listUsers(options: ListUsersOptions? = null): UserList {
    val config = RequestConfig.builder().params(options ?: ListUsersOptions()).build()
    return workos.get("/users", UserList::class.java, config)
  }

  /**
   * Parameters for the [listUsers] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class ListUsersOptions @JvmOverloads constructor(
    @JsonProperty("type") val type: UserType? = null,
    @JsonProperty("email") val email: String? = null,
    @JsonProperty("organization") val organization: String? = null,
    @JsonProperty("limit") val limit: Int? = null,
    @JsonProperty("order") val order: Order? = null,
    @JsonProperty("before") val before: String? = null,
    @JsonProperty("after") val after: String? = null
  ) : PaginationParams(after, before, limit, order) {
    init {
      if (type != null) set("type", type.toString())
      if (email != null) set("email", email)
      if (organization != null) set("organization", organization)
    }

    /**
     * Builder class for [ListUsersOptions].
     */
    class ListUsersOptionsBuilder {
      private var type: UserType? = null
      private var email: String? = null
      private var organization: String? = null
      private var limit: Int? = null
      private var order: Order? = null
      private var before: String? = null
      private var after: String? = null

      fun type(value: UserType) = apply { this.type = value }
      fun email(value: String) = apply { this.email = value }
      fun organization(value: String) = apply { this.organization = value }
      fun limit(value: Int?) = apply { this.limit = value }
      fun order(value: Order?) = apply { this.order = value }
      fun before(value: String?) = apply { this.before = value }
      fun after(value: String?) = apply { this.after = value }

      fun build(): ListUsersOptions {
        return ListUsersOptions(
          type = this.type,
          email = this.email,
          organization = this.organization,
          limit = this.limit,
          order = this.order,
          before = this.before,
          after = this.after
        )
      }
    }

    companion object {
      @JvmStatic
      fun builder(): ListUsersOptionsBuilder {
        return ListUsersOptionsBuilder()
      }
    }
  }

  /**
   * Verifies a user's session.
   */
  fun verifySession(verifySessionOptions: VerifySessionOptions): VerifySessionResponse {
    val config = RequestConfig.builder()
      .data(verifySessionOptions)
      .build()

    return workos.post("/users/sessions/verify", VerifySessionResponse::class.java, config)
  }

  /**
   * Parameters for the [verifySession] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class VerifySessionOptions @JvmOverloads constructor(
    @JsonProperty("token") val token: String,
    @JsonProperty("client_id") val clientID: String
  ) {
    init {
      require(token.isNotBlank()) { "Token is required." }
      require(clientID.isNotBlank()) { "ClientID is required." }
    }

    /**
     * Builder class for [VerifySessionOptions].
     */
    class VerifySessionOptionsBuilder {
      private lateinit var token: String
      private lateinit var clientID: String

      /**
       * Sets the token.
       */
      fun token(value: String) = apply { this.token = value }

      /**
       * Sets the client ID.
       */
      fun clientID(value: String) = apply { this.clientID = value }

      /**
       * Creates a [VerifySessionOptions] with the given builder parameters.
       */
      fun build(): VerifySessionOptions {
        return VerifySessionOptions(token, clientID)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): VerifySessionOptionsBuilder {
        return VerifySessionOptionsBuilder()
      }
    }
  }

  /**
   * Resets a user's password using the token that was sent to the user.
   */
  fun completePasswordReset(completePasswordResetOptions: CompletePasswordResetOptions): User {
    val config = RequestConfig.builder()
      .data(completePasswordResetOptions)
      .build()

    return workos.post("/users/password_reset", User::class.java, config)
  }

  /**
   * Parameters for the [completePasswordReset] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class CompletePasswordResetOptions @JvmOverloads constructor(
    @JsonProperty("token") val token: String,
    @JsonProperty("new_password") val newPassword: String
  ) {
    init {
      require(token.isNotBlank()) { "Token is required." }
      require(newPassword.isNotBlank()) { "New Password is required." }
    }

    /**
     * Builder class for [CompletePasswordResetOptions].
     */
    class CompletePasswordResetOptionsBuilder {
      private lateinit var token: String
      private lateinit var newPassword: String

      /**
       * Sets the token.
       */
      fun token(value: String) = apply { this.token = value }

      /**
       * Sets the new password.
       */
      fun newPassword(value: String) = apply { this.newPassword = value }

      /**
       * Creates a [CompletePasswordResetOptions] with the given builder parameters.
       */
      fun build(): CompletePasswordResetOptions {
        return CompletePasswordResetOptions(token, newPassword)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CompletePasswordResetOptionsBuilder {
        return CompletePasswordResetOptionsBuilder()
      }
    }
  }

  /**
   * Parameters for the [createPasswordResetChallenge] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class CreatePasswordResetChallengeOptions @JvmOverloads constructor(
    @JsonProperty("email") val email: String,
    @JsonProperty("password_reset_url") val passwordResetUrl: String
  ) {
    init {
      require(email.isNotBlank()) { "Email is required." }
      require(passwordResetUrl.isNotBlank()) { "Password Reset URL is required." }
    }

    /**
     * Builder class for [CreatePasswordResetChallengeOptions].
     */
    class CreatePasswordResetChallengeOptionsBuilder {
      private lateinit var email: String
      private lateinit var passwordResetUrl: String

      /**
       * Sets the email address.
       */
      fun email(value: String) = apply { this.email = value }

      /**
       * Sets the password reset URL.
       */
      fun passwordResetUrl(value: String) = apply { this.passwordResetUrl = value }

      /**
       * Creates a [CreatePasswordResetChallengeOptions] with the given builder parameters.
       */
      fun build(): CreatePasswordResetChallengeOptions {
        return CreatePasswordResetChallengeOptions(email, passwordResetUrl)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CreatePasswordResetChallengeOptionsBuilder {
        return CreatePasswordResetChallengeOptionsBuilder()
      }
    }
  }

  /**
   * Initiates a password reset challenge and emails a password reset link to an unmanaged user.
   */
  fun createPasswordResetChallenge(createPasswordResetChallengeOptions: CreatePasswordResetChallengeOptions): CreatePasswordResetChallengeResponse {
    val config = RequestConfig.builder()
      .data(createPasswordResetChallengeOptions)
      .build()

    return workos.post("/users/password_reset_challenge", CreatePasswordResetChallengeResponse::class.java, config)
  }

  /**
   * Parameters for the [authenticateUserWithPassword] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class AuthenticateUserWithPasswordOptions @JvmOverloads constructor(
    @JsonProperty("email") val email: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("ip_address") val ipAddress: String? = null,
    @JsonProperty("user_agent") val userAgent: String? = null,
    @JsonProperty("start_session") val startSession: Boolean? = null,
    @JsonProperty("expires_in") val expiresIn: Int? = null,
    @JsonProperty("client_secret") val clientSecret: String,
    @JsonProperty("grant_type") val grantType: String = "password",
  ) {
    init {
      require(email.isNotBlank()) { "Email is required." }
      require(password.isNotBlank()) { "Password is required." }
    }

    /**
     * Builder class for [AuthenticateUserWithPasswordOptions].
     */
    class AuthenticateUserWithPasswordOptionsBuilder {
      private lateinit var email: String
      private lateinit var password: String
      private var ipAddress: String? = null
      private var userAgent: String? = null
      private var startSession: Boolean? = null
      private var expiresIn: Int? = null

      fun email(value: String) = apply { this.email = value }
      fun password(value: String) = apply { this.password = value }
      fun ipAddress(value: String) = apply { this.ipAddress = value }
      fun userAgent(value: String) = apply { this.userAgent = value }
      fun startSession(value: Boolean) = apply { this.startSession = value }
      fun expiresIn(value: Int) = apply { this.expiresIn = value }

      fun build(): AuthenticateUserWithPasswordOptions {
        return AuthenticateUserWithPasswordOptions(email, password, ipAddress, userAgent, startSession, expiresIn, WorkOS.apiKey)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): AuthenticateUserWithPasswordOptionsBuilder {
        return AuthenticateUserWithPasswordOptionsBuilder()
      }
    }
  }

  fun authenticateUserWithPassword(opts: AuthenticateUserWithPasswordOptions): AuthenticationResponse {
    val config = RequestConfig.builder()
      .data(opts)
      .build()

    return workos.post("/users/sessions/token", AuthenticationResponse::class.java, config)
  }
}
