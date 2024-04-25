package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

open class AuthenticationAdditionalOptions(
  /**
   * The token of an invitation. The invitation should be in the pending state.
   *
   * When a valid invitation token is specified, the user is able to sign up even
   * if it is disabled in the environment. Additionally, if the invitation was for
   * a specific organization, attaching the token to a user's authenticate call
   * automatically provisions their membership to the organization.
   */
  @JsonProperty("invitation_code")
  open val invitationCode: String? = null,

  /**
   * The IP address of the request from the user who is attempting to authenticate.
   *
   * Refer to your web framework or server documentation for the correct way to
   * obtain the user’s actual IP address. If your application receives requests
   * from a reverse proxy, you may need to retrieve this from a special header
   * like `X-Forward-For`.
   */
  @JsonProperty("ip_address")
  open val ipAddress: String? = null,

  /**
   * The user agent of the request from the user who is attempting to authenticate.
   * This should be the value of the `User-Agent` header.
   */
  @JsonProperty("user_agent")
  open val userAgent: String? = null
)
