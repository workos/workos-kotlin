package com.workos.users.models
import com.workos.users.models.UserType

import com.fasterxml.jackson.annotation.JsonValue

enum class SessionAuthenticationMethod(@JsonValue val type: String) {

  /**
   * Google Oauth method.
   */
  GoogleOauth("GoogleOauth"),

  /**
   * Magic Authentication method.
   */
  MagicAuth("MagicAuth"),

  /**
   * Microsoft Oauth method.
   */
  MicrosoftOauth("MicrosoftOauth"),

  /**
   * Password method.
   */
  Password("Password")
}
