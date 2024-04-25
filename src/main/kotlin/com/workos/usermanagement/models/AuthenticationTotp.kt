package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An authentication TOTP
 *
 * @param issuer Your application or company name displayed in the user’s authenticator app. Defaults to your WorkOS team name.
 * @param user The user’s account name displayed in their authenticator app. Defaults to the user’s email.
 * @param qrCode Base64 encoded image containing scannable QR code.
 * @param secret TOTP secret that can be manually entered into some authenticator apps in place of scanning a QR code.
 * @param uri The `otpauth` URI that is encoded by the provided `qr_code`.
 */
data class AuthenticationTotp @JsonCreator constructor(
  @JsonProperty("issuer")
  val issuer: String? = null,

  @JsonProperty("user")
  val user: String? = null,

  @JsonProperty("qr_code")
  val qrCode: String? = null,

  @JsonProperty("secret")
  val secret: String? = null,

  @JsonProperty("uri")
  val uri: String? = null
)
