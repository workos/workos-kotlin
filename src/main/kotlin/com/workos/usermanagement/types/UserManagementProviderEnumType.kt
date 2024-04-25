package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class UserManagementProviderEnumType(val type: String) {
  /**
   * AuthKit
   */
  @JsonProperty("authkit")
  AuthKit("authkit"),

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
