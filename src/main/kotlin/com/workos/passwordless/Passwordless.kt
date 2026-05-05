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
    /** The email address of the user requesting a magic link. */
    val email: String,
    /** URL to redirect the user to after clicking the magic link. */
    val redirectUri: String? = null,
    /** Opaque state parameter returned in the redirect callback. */
    val state: String? = null,
    /** Connection ID to associate with this passwordless session. */
    val connection: String? = null,
    /** Number of seconds before the magic link expires. */
    val expiresIn: Int? = null,
    /** Type of passwordless session (defaults to `"MagicLink"`). */
    val type: String = "MagicLink"
  )

/** Passwordless session object returned by `create_session`. */
data class PasswordlessSession(
  /** Unique identifier for this passwordless session. */
  val id: String,
  /** Email address the magic link was sent to. */
  val email: String,
  /** ISO 8601 timestamp when the magic link expires. */
  @JsonProperty("expires_at") val expiresAt: String,
  /** The magic-link URL the user clicks to authenticate. */
  val link: String,
  /** Object type identifier (always `"passwordless_session"`). */
  val `object`: String = "passwordless_session"
)

/** Response returned by `send_session`. */
data class SendSessionResponse
  @JvmOverloads
  constructor(
    /** Human-readable status message from the API. */
    val message: String? = null,
    /** Whether the magic-link email was sent successfully. */
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
