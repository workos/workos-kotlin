package com.workos.users

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.common.models.Order
import com.workos.users.models.AuthenticationFactorList
import com.workos.users.models.ChallengeResponse
import com.workos.users.models.EnrollAuthFactorResponse
import com.workos.users.models.FactorType
import com.workos.users.models.User
import com.workos.users.models.UserList
import com.workos.users.models.UserResponse

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
    @JsonProperty("email_verified") val emailVerified: Boolean? = false
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
      private var emailVerified: Boolean? = false

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
    @JsonIgnore
    val userId: String,
    @JsonProperty("organization_id") val organization: String
  ) {
    init {
      require(userId.isNotBlank()) { "User id is required" }
      require(organization.isNotBlank()) { "Organization id is required" }
    }

    /**
     * Builder class for [AddUserToOrganizationOptions].
     */
    class AddUserToOrganizationOptionsBuilder {
      lateinit var userId: String
      lateinit var organization: String

      /**
       * Sets the user id.
       */
      fun userId(value: String) = apply { this.userId = value }

      /**
       * Sets the organization id.
       */
      fun organization(value: String) = apply { this.organization = value }

      /**
       * Creates a [AddUserToOrganizationOptions] with the given builder parameters.
       */
      fun build(): AddUserToOrganizationOptions {
        return AddUserToOrganizationOptions(userId, organization)
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
    val id = addUserToOrganizationOptions.userId
    val config = RequestConfig.builder()
      .data(addUserToOrganizationOptions)
      .build()
    return workos.post("/users/$id/organizations", User::class.java, config)
  }

  /**
   * Parameters for the [removeUserFromOrganization] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class RemoveUserFromOrganizationOptions @JvmOverloads constructor(
    @JsonProperty("id") val userId: String,
    @JsonProperty("organization_id") val organizationId: String
  ) {
    init {
      require(userId.isNotBlank()) { "User id is required" }
      require(organizationId.isNotBlank()) { "Organization id is required" }
    }

    /**
     * Builder class for [RemoveUserFromOrganizationOptions].
     */
    class RemoveUserFromOrganizationOptionsBuilder {
      private lateinit var userId: String
      private lateinit var organizationId: String

      /**
       * Sets the user id.
       */
      fun userId(value: String) = apply { this.userId = value }

      /**
       * Sets the organization id.
       */
      fun organizationId(value: String) = apply { this.organizationId = value }

      /**
       * Creates a [RemoveUserFromOrganizationOptions] with the given builder parameters.
       */
      fun build(): RemoveUserFromOrganizationOptions {
        return RemoveUserFromOrganizationOptions(userId, organizationId)
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
    val id = removeUserFromOrganizationOptions.userId
    val organization = removeUserFromOrganizationOptions.organizationId
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
    @JsonProperty("email") val email: String? = null,
    @JsonProperty("organization") val organization: String? = null,
    @JsonProperty("limit") val limit: Int? = null,
    @JsonProperty("order") val order: Order? = null,
    @JsonProperty("before") val before: String? = null,
    @JsonProperty("after") val after: String? = null
  ) : PaginationParams(after, before, limit, order) {
    init {
      if (email != null) set("email", email)
      if (organization != null) set("organization", organization)
    }

    /**
     * Builder class for [ListUsersOptions].
     */
    class ListUsersOptionsBuilder {
      private var email: String? = null
      private var organization: String? = null
      private var limit: Int? = null
      private var order: Order? = null
      private var before: String? = null
      private var after: String? = null

      fun email(value: String) = apply { this.email = value }
      fun organization(value: String) = apply { this.organization = value }
      fun limit(value: Int?) = apply { this.limit = value }
      fun order(value: Order?) = apply { this.order = value }
      fun before(value: String?) = apply { this.before = value }
      fun after(value: String?) = apply { this.after = value }

      fun build(): ListUsersOptions {
        return ListUsersOptions(
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
   * Resets a user's password using the token that was sent to the user.
   */
  fun resetPassword(resetPasswordOptions: ResetPasswordOptions): UserResponse {
    val config = RequestConfig.builder()
      .data(resetPasswordOptions)
      .build()

    return workos.post("/users/password_reset", UserResponse::class.java, config)
  }

  /**
   * Parameters for the [resetPassword] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class ResetPasswordOptions @JvmOverloads constructor(
    @JsonProperty("token") val token: String,
    @JsonProperty("new_password") val newPassword: String
  ) {
    init {
      require(token.isNotBlank()) { "Token is required." }
      require(newPassword.isNotBlank()) { "New Password is required." }
    }

    /**
     * Builder class for [ResetPasswordOptions].
     */
    class ResetPasswordOptionsBuilder {
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
       * Creates a [ResetPasswordOptions] with the given builder parameters.
       */
      fun build(): ResetPasswordOptions {
        return ResetPasswordOptions(token, newPassword)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ResetPasswordOptionsBuilder {
        return ResetPasswordOptionsBuilder()
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
  fun createPasswordResetChallenge(createPasswordResetChallengeOptions: CreatePasswordResetChallengeOptions): ChallengeResponse {
    val config = RequestConfig.builder()
      .data(createPasswordResetChallengeOptions)
      .build()

    return workos.post("/users/password_reset_challenge", ChallengeResponse::class.java, config)
  }

  /**
   * Parameters for the [authenticateUserWithPassword] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class AuthenticateUserWithPasswordOptions @JvmOverloads constructor(
    @JsonProperty("email") val email: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("ip_address") val ipAddress: String? = null,
    @JsonProperty("user_agent") val userAgent: String? = null,
    @JsonProperty("client_secret") val clientSecret: String? = null,
    @JsonProperty("grant_type") val grantType: String = "password",
  ) {
    init {
      require(clientId.isNotBlank()) { "ClientID is required." }
      require(email.isNotBlank()) { "Email is required." }
      require(password.isNotBlank()) { "Password is required." }
    }

    /**
     * Builder class for [AuthenticateUserWithPasswordOptions].
     */
    class AuthenticateUserWithPasswordOptionsBuilder {
      private lateinit var email: String
      private lateinit var password: String
      private lateinit var clientId: String
      private var ipAddress: String? = null
      private var userAgent: String? = null
      private var clientSecret: String? = null

      fun email(value: String) = apply { this.email = value }
      fun password(value: String) = apply { this.password = value }
      fun clientId(value: String) = apply { this.clientId = value }
      fun ipAddress(value: String) = apply { this.ipAddress = value }
      fun userAgent(value: String) = apply { this.userAgent = value }

      fun build(): AuthenticateUserWithPasswordOptions {
        return AuthenticateUserWithPasswordOptions(email, password, clientId, ipAddress, userAgent, clientSecret)
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

  fun authenticateUserWithPassword(authenticateUserWithPasswordOptions: AuthenticateUserWithPasswordOptions): UserResponse {

    val updatedOptions = authenticateUserWithPasswordOptions.copy(clientSecret = workos.apiKey)

    val config = RequestConfig.builder()
      .data(updatedOptions)
      .build()

    return workos.post("/users/authenticate", UserResponse::class.java, config)
  }

  /**
   * Parameters for the [authenticateUserWithCode] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class AuthenticateUserWithCodeOptions @JvmOverloads constructor(
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("code") val code: String,
    @JsonProperty("ip_address") val ipAddress: String? = null,
    @JsonProperty("user_agent") val userAgent: String? = null,
    @JsonProperty("client_secret") val clientSecret: String? = null,
    @JsonProperty("grant_type") val grantType: String = "authorization_code",
  ) {
    init {
      require(code.isNotBlank()) { "Code is required." }
      require(clientId.isNotBlank()) { "ClientID is required." }
    }

    /**
     * Builder class for [AuthenticateUserWithCodeOptions].
     */
    class AuthenticateUserWithCodeOptionsBuilder {
      private lateinit var clientId: String
      private lateinit var code: String
      private var ipAddress: String? = null
      private var userAgent: String? = null
      private var clientSecret: String? = null

      fun clientId(value: String) = apply { this.clientId = value }
      fun code(value: String) = apply { this.code = value }
      fun ipAddress(value: String) = apply { this.ipAddress = value }
      fun userAgent(value: String) = apply { this.userAgent = value }

      fun build(): AuthenticateUserWithCodeOptions {
        return AuthenticateUserWithCodeOptions(clientId, code, ipAddress, userAgent, clientSecret)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): AuthenticateUserWithCodeOptionsBuilder {
        return AuthenticateUserWithCodeOptionsBuilder()
      }
    }
  }

  fun authenticateUserWithCode(authenticateUserWithCodeOptions: AuthenticateUserWithCodeOptions): UserResponse {

    val updatedOptions = authenticateUserWithCodeOptions.copy(clientSecret = workos.apiKey)

    val config = RequestConfig.builder()
      .data(updatedOptions)
      .build()

    return workos.post("/users/authenticate", UserResponse::class.java, config)
  }

  /**
   * Parameters for the [authenticateUserWithMagicAuth] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class AuthenticateUserWithMagicAuthOptions @JvmOverloads constructor(
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("code") val code: String,
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("ip_address") val ipAddress: String? = null,
    @JsonProperty("user_agent") val userAgent: String? = null,
    @JsonProperty("client_secret") val clientSecret: String? = null,
    @JsonProperty("grant_type") val grantType: String = "urn:workos:oauth:grant-type:magic-auth:code",
  ) {
    init {
      require(code.isNotBlank()) { "Code is required." }
      require(clientId.isNotBlank()) { "ClientID is required." }
      require(userId.isNotBlank()) { "Magic auth challenge ID is required." }
    }

    /**
     * Builder class for [AuthenticateUserWithMagicAuthOptions].
     */
    class AuthenticateUserWithMagicAuthOptionsBuilder {
      private lateinit var clientId: String
      private lateinit var code: String
      private lateinit var userId: String
      private var ipAddress: String? = null
      private var userAgent: String? = null
      private var clientSecret: String? = null

      fun clientId(value: String) = apply { this.clientId = value }
      fun code(value: String) = apply { this.code = value }
      fun userId(value: String) = apply { this.userId = value }
      fun ipAddress(value: String) = apply { this.ipAddress = value }
      fun userAgent(value: String) = apply { this.userAgent = value }

      fun build(): AuthenticateUserWithMagicAuthOptions {
        return AuthenticateUserWithMagicAuthOptions(clientId, code, userId, ipAddress, userAgent, clientSecret)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): AuthenticateUserWithMagicAuthOptionsBuilder {
        return AuthenticateUserWithMagicAuthOptionsBuilder()
      }
    }
  }

  /**
   * Parameters for the [authenticateUserWithTotp] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class AuthenticateUserWithTotpOptions @JvmOverloads constructor(
    @JsonProperty("pending_authentication_token") val pendingAuthenticationToken: String,
    @JsonProperty("authentication_challenge_id") val authenticationChallengeId: String,
    @JsonProperty("code") val code: String,
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("client_secret") val clientSecret: String? = null,
    @JsonProperty("grant_type") val grantType: String = "urn:workos:oauth:grant-type:mfa-totp",
  ) {
    init {
      require(clientId.isNotBlank()) { "ClientID is required." }
      require(pendingAuthenticationToken.isNotBlank()) { "Email is required." }
      require(authenticationChallengeId.isNotBlank()) { "Authentication Challenge Id is required." }
      require(code.isNotBlank()) { "Code is required." }
    }

    /**
     * Builder class for [AuthenticateUserWithTotpOptions].
     */
    class AuthenticateUserWithTotpOptionsBuilder {
      private lateinit var pendingAuthenticationToken: String
      private lateinit var authenticationChallengeId: String
      private lateinit var code: String
      private lateinit var clientId: String
      private var clientSecret: String? = null

      fun pendingAuthenticationToken(value: String) = apply { this.pendingAuthenticationToken = value }
      fun authenticationChallengeId(value: String) = apply { this.authenticationChallengeId = value }

      fun code(value: String) = apply { this.code = value }
      fun clientId(value: String) = apply { this.clientId = value }

      fun build(): AuthenticateUserWithTotpOptions {
        return AuthenticateUserWithTotpOptions(pendingAuthenticationToken, authenticationChallengeId, code, clientId, clientSecret)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): AuthenticateUserWithTotpOptionsBuilder {
        return AuthenticateUserWithTotpOptionsBuilder()
      }
    }
  }

  fun authenticateUserWithTotp(authenticateUserWithTotpOptions: AuthenticateUserWithTotpOptions): UserResponse {

    val updatedOptions = authenticateUserWithTotpOptions.copy(clientSecret = workos.apiKey)

    val config = RequestConfig.builder()
      .data(updatedOptions)
      .build()

    return workos.post("/users/authenticate", UserResponse::class.java, config)
  }

  fun authenticateUserWithMagicAuth(authenticateUserWithMagicAuthOptions: AuthenticateUserWithMagicAuthOptions): UserResponse {

    val updatedOptions = authenticateUserWithMagicAuthOptions.copy(clientSecret = workos.apiKey)

    val config = RequestConfig.builder()
      .data(updatedOptions)
      .build()

    return workos.post("/users/authenticate", UserResponse::class.java, config)
  }

  /**
   * Parameters for the [sendMagicAuthCode] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  data class SendMagicAuthCodeOptions @JvmOverloads constructor(
    @JsonProperty("email") val email: String,
  ) {
    init {
      require(email.isNotBlank()) { "Email is required." }
    }

    /**
     * Builder class for [ SendMagicAuthCodeOptions].
     */
    class SendMagicAuthCodeBuilder {
      private lateinit var email: String

      fun email(value: String) = apply { this.email = value }

      fun build(): SendMagicAuthCodeOptions {
        return SendMagicAuthCodeOptions(email)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): SendMagicAuthCodeBuilder {
        return SendMagicAuthCodeBuilder()
      }
    }
  }

  fun sendMagicAuthCode(sendMagicAuthCodeOptions: SendMagicAuthCodeOptions): User {

    val config = RequestConfig.builder()
      .data(sendMagicAuthCodeOptions)
      .build()

    return workos.post("/users/magic_auth/send", User::class.java, config)
  }

  /**
   * Parameters for the [verifyEmailCode] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class VerifyEmailCodeOptions @JvmOverloads constructor(
    @JsonIgnore
    val userId: String,
    @JsonProperty("code") val code: String
  ) {
    init {
      require(userId.isNotBlank()) { "User ID is required" }
      require(code.isNotBlank()) { "Code is required" }
    }

    /**
     * Builder class for [VerifyEmailCodeOptions].
     */
    class VerifyEmailCodeOptionsBuilder {
      private lateinit var userId: String
      private lateinit var code: String

      /**
       * Sets the id.
       */
      fun userId(value: String) = apply { this.userId = value }

      /**
       * Sets the code.
       */
      fun code(value: String) = apply { this.code = value }

      /**
       * Creates a [VerifyEmailCodeOptions] with the given builder parameters.
       */
      fun build(): VerifyEmailCodeOptions {
        return VerifyEmailCodeOptions(userId, code)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): VerifyEmailCodeOptionsBuilder {
        return VerifyEmailCodeOptionsBuilder()
      }
    }
  }
  fun verifyEmailCode(verifyEmailCodeOptions: VerifyEmailCodeOptions): UserResponse {
    val id = verifyEmailCodeOptions.userId
    val config = RequestConfig.builder().data(verifyEmailCodeOptions).build()
    return workos.post("users/$id/verify_email_code", UserResponse::class.java, config)
  }

  fun sendVerificationEmail(id: String): User {
    return workos.post("/users/$id/send_verification_email", User::class.java)
  }

  /**
   * Parameters for the [updateUserPassword] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class UpdateUserPasswordOptions @JvmOverloads constructor(
    @JsonIgnore
    val userId: String,
    @JsonProperty("password") val password: String
  ) {
    init {
      require(userId.isNotBlank()) { "User id is required" }
      require(password.isNotBlank()) { "Password is required" }
    }

    /**
     * Builder class for [UpdateUserPasswordOptions].
     */
    class UpdateUserPasswordOptionsBuilder {
      private lateinit var userId: String
      private lateinit var password: String

      /**
       * Sets the User Id.
       */
      fun userId(value: String) = apply { this.userId = value }

      /**
       * Sets the password.
       */
      fun password(value: String) = apply { this.password = value }

      /**
       * Creates an [UpdateUserPasswordOptions] with the given builder parameters.
       */
      fun build(): UpdateUserPasswordOptions {
        return UpdateUserPasswordOptions(userId, password)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): UpdateUserPasswordOptionsBuilder {
        return UpdateUserPasswordOptionsBuilder()
      }
    }
  }

  /**
   * Updates the password of a specified user.
   */
  fun updateUserPassword(
    updateUserPasswordOptions: UpdateUserPasswordOptions
  ): User {
    val id = updateUserPasswordOptions.userId
    val config = RequestConfig.builder().data(updateUserPasswordOptions).build()
    return workos.put("/users/$id/password", User::class.java, config)
  }

  fun deleteUser(id: String) {
    workos.delete("/users/$id")
  }

  /**
   * Gets a list of AuthenticationFactors for a user.
   */
  fun listAuthFactors(id: String): AuthenticationFactorList {
    return workos.get("/users/$id/auth/factors", AuthenticationFactorList::class.java)
  }

  /**
   * Parameters for the [enrollAuthFactor] method.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class EnrollAuthFactorOptions @JvmOverloads constructor(
    @JsonIgnore
    val userId: String,
    @JsonProperty("type") val type: FactorType
  ) {
    init {
      require(userId.isNotBlank()) { "User id is required" }
    }

    /**
     * Builder class for [EnrollAuthFactorOptions].
     */
    class EnrollAuthFactorOptionsBuilder {
      private lateinit var userId: String
      private lateinit var type: FactorType

      /**
       * Sets the User Id.
       */
      fun userId(value: String) = apply { this.userId = value }

      /**
       * Sets the password.
       */
      fun type(value: FactorType) = apply { this.type = value }

      /**
       * Creates an [EnrollAuthFactorOptions] with the given builder parameters.
       */
      fun build(): EnrollAuthFactorOptions {
        return EnrollAuthFactorOptions(userId, type)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): EnrollAuthFactorOptionsBuilder {
        return EnrollAuthFactorOptionsBuilder()
      }
    }
  }
  /**
   * Enrolls an AuthenticationFactors for a user.
   */
  fun enrollAuthFactor(enrollAuthFactorOptions: EnrollAuthFactorOptions): EnrollAuthFactorResponse {
    val id = enrollAuthFactorOptions.userId
    val config = RequestConfig.builder()
      .data(enrollAuthFactorOptions)
      .build()
    return workos.post("/users/$id/auth/factors", EnrollAuthFactorResponse::class.java, config)
  }
}
