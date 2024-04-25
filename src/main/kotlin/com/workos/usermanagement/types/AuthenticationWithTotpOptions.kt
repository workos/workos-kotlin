package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class AuthenticationWithTotpOptions @JvmOverloads constructor(
  /**
   * Identifies the application making the request to the WorkOS server.
   */
  @JsonProperty("client_id")
  val clientId: String,

  /**
   * Authenticates the application making the request to the WorkOS server.
   */
  @JsonProperty("client_secret")
  val clientSecret: String,

  /**
   * A string constant that distinguishes the method by which your application
   * will receive an access token.
   */
  @JsonProperty("grant_type")
  val grantType: String,

  /**
   * The one-time code that was emailed to the user.
   */
  @JsonProperty("code")
  val code: String,

  /**
   * The unique ID of the authentication challenge created for the TOTP factor for which the user is enrolled.
   */
  @JsonProperty("authentication_challenge_id")
  val authenticationChallengeId: String,

  /**
   * The authentication token returned from a failed authentication attempt due to the corresponding error.
   */
  @JsonProperty("pending_authentication_token")
  val pendingAuthenticationToken: String,

  @JsonProperty("invitation_code")
  override val invitationCode: String? = null,

  @JsonProperty("ip_address")
  override val ipAddress: String? = null,

  @JsonProperty("user_agent")
  override val userAgent: String? = null
) : AuthenticationAdditionalOptions(invitationCode, ipAddress, userAgent) {
  init {
    require(clientId.isNotBlank()) { "Client ID is required" }
    require(clientSecret.isNotBlank()) { "Client Secret is required" }
    require(authenticationChallengeId.isNotBlank()) { "Authentication Challenge ID is required" }
    require(pendingAuthenticationToken.isNotBlank()) { "Pending Authentication Token is required" }
  }
}
