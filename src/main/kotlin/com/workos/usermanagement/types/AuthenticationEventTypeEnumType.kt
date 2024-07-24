package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class AuthenticationEventTypeEnumType(val type: String) {
  /**
   * Email Verification
   */
  @JsonProperty("email_verification")
  EmailVerification("email_verification"),

  /**
   * Magic Auth
   */
  @JsonProperty("magic_auth")
  MagicAuth("magic_auth"),

  /**
   * MFA
   */
  @JsonProperty("mfa")
  MFA("mfa"),

  /**
   * OAuth
   */
  @JsonProperty("oauth")
  OAuth("oauth"),

  /**
   * Password
   */
  @JsonProperty("password")
  Password("password"),

  /**
   * SSO
   */
  @JsonProperty("sso")
  SSO("sso"),
}
