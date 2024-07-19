package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class EnrolledAuthenticationFactorOptions @JvmOverloads constructor(
  /**
   * The type of the factor to enroll. The only available option is TOTP.
   */
  @JsonProperty("type")
  val type: String = "totp",

  /**
   * Your application or company name displayed in the user’s authenticator app. Defaults to your WorkOS team name.
   */
  @JsonProperty("totp_issuer")
  val totpIssuer: String? = null,

  /**
   * The user’s account name displayed in their authenticator app. Defaults to the user’s email.
   */
  @JsonProperty("totp_user")
  val totpUser: String? = null,
)
