package com.workos.usermanagement
import com.workos.WorkOS
import org.apache.http.client.utils.URIBuilder

class UserManagementApi(private val workos: WorkOS) {
  /**
   * Builder for authorization URLs. Can be created via [getAuthorizationUrl] method.
   *
   * @param baseUrl The base URL to retrieve the OAuth 2.0 authorization from.
   * @param clientId This value can be obtained from the Configuration page in the WorkOS dashboard.
   * @param redirectUri A redirect URI to return an authorized user to.
   * @param connection Connection ID to determine which identity provider to authenticate with.
   * @param domain Use the domain to determine which connection and identity provider to authenticate with.
   * @param provider Name of the identity provider e.g. GitHubOAuth, GoogleOAuth, or MicrosoftOAuth.
   * @param state User defined information to persist application data between redirects.
   */
  class AuthorizationUrlOptionsBuilder @JvmOverloads constructor(
    val baseUrl: String,
    val clientId: String,
    val redirectUri: String,
    var connectionId: String? = null,
    var domainHint: String? = null,
    var loginHint: String? = null,
    var organizationId: String? = null,
    var provider: String? = null,
    var state: String? = null
  ) {
    /**
     * Connection ID.
     */
    fun connectionId(value: String) = apply { connectionId = value }

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
    fun organizationId(value: String) = apply { organizationId = value }

    /**
     * Value used to authenticate all users with the same connection and Identity Provider.
     *
     * Currently, the only supported values for provider are GitHubOAuth, GoogleOAuth, and
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
        .setPath("/user_management/authorize")
        .addParameter("client_id", clientId)
        .addParameter("redirect_uri", redirectUri)
        .addParameter("response_type", "code")

      if (connectionId != null) uriBuilder.addParameter("connection_id", connectionId)
      if (domainHint != null) uriBuilder.addParameter("domain_hint", domainHint)
      if (loginHint != null) uriBuilder.addParameter("login_hint", loginHint)
      if (organizationId != null) uriBuilder.addParameter("organization_id", organizationId)
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
   * Generate an Oauth2 authorization URL where your users will
   * authenticate using the configured SSO Identity Provider.
   */
  fun getAuthorizationUrl(clientId: String, redirectUri: String): AuthorizationUrlOptionsBuilder {
    return AuthorizationUrlOptionsBuilder.create(workos.baseUrl, clientId, redirectUri)
  }
}
