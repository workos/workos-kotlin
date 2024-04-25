package com.workos.usermanagement.builders

import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.AuthenticationWithOrganizationSelectionOptions

/**
 * Builder for options when authenticating with organization selection.
 *
 * @param clientId Identifies the application making the request to the WorkOS server.
 * @param clientSecret Authenticates the application making the request to the WorkOS server.
 * @param organizationId The organization the user selected to sign in to.
 * @param pendingAuthenticationToken The authentication token returned from a failed authentication attempt due to the corresponding error.
 * @param options The authentication options passed to the authentication request.
 */
class AuthenticationWithOrganizationSelectionOptionsBuilder(
  private val clientId: String,
  private val clientSecret: String,
  private val organizationId: String,
  private val pendingAuthenticationToken: String,
  private val options: AuthenticationAdditionalOptions? = null
) {
  /**
   * Generates the AuthenticationWithOrganizationSelectionOptions object.
   */
  fun build(): AuthenticationWithOrganizationSelectionOptions {
    return AuthenticationWithOrganizationSelectionOptions(
      clientId = this.clientId,
      clientSecret = this.clientSecret,
      grantType = "urn:workos:oauth:grant-type:organization-selection",
      organizationId = this.organizationId,
      pendingAuthenticationToken = this.pendingAuthenticationToken,
      invitationCode = this.options?.invitationCode,
      ipAddress = this.options?.ipAddress,
      userAgent = this.options?.userAgent,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(clientId: String, clientSecret: String, code: String, pendingAuthenticationToken: String, options: AuthenticationAdditionalOptions? = null): AuthenticationWithOrganizationSelectionOptionsBuilder {
      return AuthenticationWithOrganizationSelectionOptionsBuilder(clientId, clientSecret, code, pendingAuthenticationToken, options)
    }
  }
}
