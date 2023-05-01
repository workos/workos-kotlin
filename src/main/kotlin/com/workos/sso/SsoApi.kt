package com.workos.sso

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.common.models.Order
import com.workos.sso.models.Connection
import com.workos.sso.models.ConnectionList
import com.workos.sso.models.ConnectionType
import com.workos.sso.models.Profile
import com.workos.sso.models.ProfileAndToken
import org.apache.http.client.utils.URIBuilder

/**
 * The SsoApi class provides convenience methods for working with the WorkOS
 * SSO platform. You'll need a valid API key, a client ID, and to have
 * created an SSO connection on your WorkOS dashboard.
 */
class SsoApi(private val workos: WorkOS) {
  /**
   * Builder for authorization URLs. Can be created via [getAuthorizationUrl] method.
   *
   * @param baseUrl The base URL to retrieve the OAuth 2.0 authorization from.
   * @param clientId This value can be obtained from the Configuration page in the WorkOS dashboard.
   * @param redirectUri A redirect URI to return an authorized user to.
   * @param connection Connection ID to determine which identity provider to authenticate with.
   * @param domain Use the domain to determine which connection and identity provider to authenticate with.
   * @param provider Name of the identity provider e.g. GoogleOAuth or MicrosoftOAuth.
   * @param state User defined information to persist application data between redirects.
   */
  class AuthorizationUrlOptionsBuilder @JvmOverloads constructor(
    val baseUrl: String,
    val clientId: String,
    val redirectUri: String,
    var connection: String? = null,
    var domain: String? = null,
    var domainHint: String? = null,
    var loginHint: String? = null,
    var organization: String? = null,
    var provider: String? = null,
    var state: String? = null
  ) {
    /**
     * Connection ID.
     */
    fun connection(value: String) = apply { connection = value }

    /**
     * Value used to identify the correct connection and authentication strategy
     * for an authenticating user.
     *
     * Provide the domain parameter when authenticating a user by their domain.
     * For example, when a user enters their email address into a login form,
     * the Developer would parse this email address for the domain and pass the domain
     * as a domain parameter into their authorization URL. Then, WorkOS will use this
     * domain value as a key to determine the connection and IdP to direct the user
     * to for authentication.
     */
    @Deprecated("Please use connection, organization, or provider.")
    fun domain(value: String) = apply { domain = value }

    /**
     * Domain Hint
     */
    fun domainHint(value: String) = apply { domainHint = value }

    /**
     * Login Hint
     */
    fun loginHint(value: String) = apply { loginHint = value }

    /**
     * Organization ID
     */
    fun organization(value: String) = apply { organization = value }

    /**
     * Value used to authenticate all users with the same connection and Identity Provider.
     *
     * Currently, the only supported values for provider are GoogleOAuth and
     * MicrosoftOAuth. Provide the provider parameter when authenticating Google OAuth
     * users, because Google OAuth does not take a user’s domain into account when logging
     * in with a “Sign in with Google” button.
     */
    fun provider(value: String) = apply { provider = value }

    /**
     * Optional parameter that a Developer can choose to include in their authorization URL.
     * If included, then the redirect URI received from WorkOS will contain the exact state
     * that was passed in the authorization URL.
     *
     * The state parameter can be used to encode arbitrary information to help restore
     * application state between redirects.
     */
    fun state(value: String) = apply { state = value }

    /**
     * Generates the URL based on the given options.
     */
    fun build(): String {
      val uriBuilder = URIBuilder("$baseUrl")
        .setPath("/sso/authorize")
        .addParameter("client_id", clientId)
        .addParameter("redirect_uri", redirectUri)
        .addParameter("response_type", "code")

      if (connection != null) uriBuilder.addParameter("connection", connection)
      if (domain != null) uriBuilder.addParameter("domain", domain)
      if (domainHint != null) uriBuilder.addParameter("domain_hint", domainHint)
      if (loginHint != null) uriBuilder.addParameter("login_hint", loginHint)
      if (organization != null) uriBuilder.addParameter("organization", organization)
      if (provider != null) uriBuilder.addParameter("provider", provider)
      if (state != null) uriBuilder.addParameter("state", state)

      return uriBuilder.toString()
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun create(baseUrl: String, clientId: String, redirectUri: String): AuthorizationUrlOptionsBuilder {
        return AuthorizationUrlOptionsBuilder(baseUrl, clientId, redirectUri)
      }
    }
  }

  /**
   * Deletes a single connection by id.
   */
  fun deleteConnection(id: String) {
    workos.delete("/connections/$id")
  }

  /**
   * Generate an Oauth2 authorization URL where your users will
   * authenticate using the configured SSO Identity Provider.
   */
  fun getAuthorizationUrl(clientId: String, redirectUri: String): AuthorizationUrlOptionsBuilder {
    return AuthorizationUrlOptionsBuilder.create(workos.baseUrl, clientId, redirectUri)
  }

  /**
   * Fetches a single connection by id.
   */
  fun getConnection(id: String): Connection {
    return workos.get("/connections/$id", Connection::class.java)
  }

  private class ProfileAndTokenOptions(
    val code: String,

    @JsonProperty("client_id")
    val clientId: String,

    @JsonProperty("client_secret")
    val clientSecret: String
  ) {
    @JsonProperty("grant_type")
    val grantType = "authorization_code"
  }

  /**
   * Fetch the profile details and access token for the authenticated SSO user.
   */
  fun getProfileAndToken(code: String, clientId: String): ProfileAndToken {
    val config = RequestConfig.builder()
      .data(ProfileAndTokenOptions(code, clientId, workos.apiKey))
      .build()

    return workos.post("/sso/token", ProfileAndToken::class.java, config)
  }

  /**
   * Fetch the profile details via provided access token.
   */
  fun getProfile(accessToken: String): Profile {
    val headers = mapOf("Authorization" to "Bearer $accessToken")

    val config = RequestConfig.builder()
      .headers(headers)
      .build()

    return workos.get("/sso/profile", Profile::class.java, config)
  }

  /**
   * Parameters for the [listConnections] method.
   */
  class ListConnectionsOptions @JvmOverloads constructor(
    connectionType: ConnectionType? = null,
    domain: String? = null,
    organizationId: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null,
    order: Order? = null
  ) : PaginationParams(after, before, limit, order) {
    init {
      if (connectionType != null) set("connection_type", connectionType.toString())
      if (domain != null) set("domain", domain)
      if (organizationId != null) set("organization_id", organizationId)
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListConnectionsOptionsPaginationParamsBuilder {
        return ListConnectionsOptionsPaginationParamsBuilder()
      }
    }

    /**
     * Parameters builder for [listConnections] method.
     */
    class ListConnectionsOptionsPaginationParamsBuilder : PaginationParams.PaginationParamsBuilder<ListConnectionsOptions>(ListConnectionsOptions()) {
      /**
       * The [ConnectionType] to filter on.
       */
      fun connectionType(value: ConnectionType) = apply { this.params["connection_type"] = value.toString() }

      /**
       * The domain to filter on.
       */
      fun domain(value: String) = apply { this.params["domain"] = value }

      /**
       * The id of an [com.workos.organizations.models.Organization] to filter on.
       */
      fun organizationId(value: String) = apply { this.params["organization_id"] = value }
    }
  }

  /**
   * Fetches list of connections.
   */
  @JvmOverloads
  fun listConnections(options: ListConnectionsOptions? = null): ConnectionList {
    val config = RequestConfig.builder()
      .params(options ?: ListConnectionsOptions())
      .build()

    return workos.get("/connections", ConnectionList::class.java, config)
  }
}
