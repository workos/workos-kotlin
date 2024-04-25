package com.workos.usermanagement.builders

import com.workos.usermanagement.types.UserManagementProviderEnumType
import org.apache.http.client.utils.URIBuilder
import java.lang.IllegalArgumentException

/**
 * Builder for options when creating Authorization URLs.
 *
 * @param baseUrl The base URL to retrieve the OAuth 2.0 authorization from.
 * @param clientId This value can be obtained from the Configuration page in the WorkOS dashboard.
 * @param redirectUri A redirect URI to return an authorized user to.
 * @param connectionId Connection ID to determine which identity provider to authenticate with.
 * @param domainHint Use the domain to determine which connection and identity provider to authenticate with.
 * @param loginHint Can be used to pre-fill the username/email address field of the IdP sign-in page for the user, if you know their username ahead of time.
 * @param screenHint Specify which AuthKit screen users should land on upon redirection (Only applicable when provider is 'authkit').
 * @param organizationId Used to initiate SSO for an organization. The value should be a WorkOS organization ID.
 * @param provider Name of the identity provider e.g. Authkit, GitHubOAuth, GoogleOAuth, or MicrosoftOAuth.
 * @param state User defined information to persist application data between redirects.
 */
class AuthorizationUrlOptionsBuilder @JvmOverloads constructor(
  private val baseUrl: String,
  private val clientId: String,
  private val redirectUri: String,
  private var connectionId: String? = null,
  private var domainHint: String? = null,
  private var loginHint: String? = null,
  private var screenHint: String? = null,
  private var organizationId: String? = null,
  private var provider: UserManagementProviderEnumType? = null,
  private var state: String? = null
) {
  /**
   * Connection ID.
   */
  fun connectionId(value: String) = apply { connectionId = value }

  /**
   * Domain Hint.
   */
  fun domainHint(value: String) = apply { domainHint = value }

  /**
   * Login Hint.
   */
  fun loginHint(value: String) = apply { loginHint = value }

  /**
   * Screen Hint.
   */
  fun screenHint(value: String) = apply { screenHint = value }

  /**
   * Organization ID.
   *
   * You can persist the WorkOS organization ID with application user or team identifiers.
   * WorkOS will use the organization ID to determine the appropriate connection and the
   * IdP to direct the user to for authentication.
   */
  fun organizationId(value: String) = apply { organizationId = value }

  /**
   * Value used to authenticate all users with the same connection and Identity Provider.
   *
   * Provide the provider parameter when authenticating Google OAuth
   * users, because Google OAuth does not take a user’s domain into account when logging
   * in with a “Sign in with Google” button.
   */
  fun provider(value: UserManagementProviderEnumType) = apply { provider = value }

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
    if (provider == null && connectionId == null && organizationId == null) {
      throw IllegalArgumentException("Incomplete arguments. Need to specify either a 'connectionId', 'organizationId', or 'provider'.")
    }

    if (provider != null && provider!!.type != "authkit" && screenHint != null) {
      throw IllegalArgumentException("'screenHint' is only supported for 'authkit' provider")
    }

    val uriBuilder = URIBuilder(baseUrl)
      .setPath("/user_management/authorize")
      .addParameter("client_id", clientId)
      .addParameter("redirect_uri", redirectUri)
      .addParameter("response_type", "code")

    if (connectionId != null) uriBuilder.addParameter("connection_id", connectionId)
    if (domainHint != null) uriBuilder.addParameter("domain_hint", domainHint)
    if (loginHint != null) uriBuilder.addParameter("login_hint", loginHint)
    if (screenHint != null) uriBuilder.addParameter("screen_hint", screenHint)
    if (organizationId != null) uriBuilder.addParameter("organization_id", organizationId)
    if (provider != null) uriBuilder.addParameter("provider", provider!!.type)
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
