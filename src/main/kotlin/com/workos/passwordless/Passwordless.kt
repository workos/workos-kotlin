// @oagen-ignore-file
package com.workos.passwordless

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.common.http.RequestOptions

/** Options for creating a magic-link passwordless session. */
data class CreatePasswordlessSessionOptions
  @JvmOverloads
  constructor(
    val email: String,
    val redirectUri: String? = null,
    val state: String? = null,
    val connection: String? = null,
    val expiresIn: Int? = null,
    val type: String = "MagicLink"
  )

/** Passwordless session object returned by `create_session`. */
data class PasswordlessSession(
  val id: String,
  val email: String,
  @JsonProperty("expires_at") val expiresAt: String,
  val link: String,
  val `object`: String = "passwordless_session"
)

/** Response returned by `send_session`. */
data class SendSessionResponse
  @JvmOverloads
  constructor(
    val message: String? = null,
    val success: Boolean? = null
  )

/**
 * Passwordless (magic-link) session helper.
 *
 * This surface is hand-maintained because the passwordless endpoints are not
 * yet in the OpenAPI spec.
 */
class Passwordless(
  private val workos: WorkOS
) {
  /** Create a magic-link passwordless session. */
  @JvmOverloads
  fun createSession(
    options: CreatePasswordlessSessionOptions,
    requestOptions: RequestOptions? = null
  ): PasswordlessSession {
    val body = linkedMapOf<String, Any?>()
    body["type"] = options.type
    body["email"] = options.email
    if (options.redirectUri != null) body["redirect_uri"] = options.redirectUri
    if (options.state != null) body["state"] = options.state
    if (options.connection != null) body["connection"] = options.connection
    if (options.expiresIn != null) body["expires_in"] = options.expiresIn
    val config =
      RequestConfig(
        method = "POST",
        path = "/passwordless/sessions",
        body = body,
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, PasswordlessSession::class.java)
  }

  /** Send the magic-link email for a previously created session. */
  @JvmOverloads
  fun sendSession(
    sessionId: String,
    requestOptions: RequestOptions? = null
  ): SendSessionResponse {
    val config =
      RequestConfig(
        method = "POST",
        path = "/passwordless/sessions/$sessionId/send",
        body = linkedMapOf<String, Any?>(),
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, SendSessionResponse::class.java)
  }
}

/** Hand-maintained accessor on the WorkOS client. */
val WorkOS.passwordless: Passwordless
  get() = service(Passwordless::class) { Passwordless(this) }
