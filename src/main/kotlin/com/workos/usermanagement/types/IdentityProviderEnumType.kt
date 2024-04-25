package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class IdentityProviderEnumType(val type: String) {
  /**
   * GitHub OAuth
   */
  @JsonProperty("GitHubOAuth")
  GitHubOAuth("GitHubOAuth"),

  /**
   * Google OAuth
   */
  @JsonProperty("GoogleOAuth")
  GoogleOAuth("GoogleOAuth"),

  /**
   * Microsoft OAuth
   */
  @JsonProperty("MicrosoftOAuth")
  MicrosoftOAuth("MicrosoftOAuth")
}
