package com.workos.passwordless

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.passwordless.models.PasswordlessSession
import com.workos.passwordless.models.SendSessionResponse
import com.workos.passwordless.models.SessionType
import java.lang.IllegalArgumentException

/**
 * The PasswordlessApi class provides convenience methods for working with WorkOS
 * passwordless sessions including the WorkOS Magic Link. You'll need a valid
 * API key.
 */
class PasswordlessApi(private val workos: WorkOS) {
  /**
   * Creates a passwordless session.
   */
  fun createSession(createSessionOptions: CreateSessionOptions): PasswordlessSession {
    val config = RequestConfig
      .builder()
      .data(createSessionOptions)
      .build()

    return workos.post(
      "/passwordless/sessions",
      PasswordlessSession::class.java,
      config
    )
  }

  /**
   * Sends a passwordless session created with [createSession] via email.
   * In the case of Magic Link, WorkOS will send an e-mail to the user with a unique
   * link to authenticate with.
   */
  fun sendSession(id: String): SendSessionResponse {
    return workos.post(
      "/passwordless/sessions/$id/send",
      SendSessionResponse::class.java
    )
  }

  /**
   * Parameters for [createSession].
   *
   * @param email The email to send the passwordless session to.
   * @param type The type of passwordless session.
   * @param connection The unique identifier of the passwordless session [com.workos.sso.models.Connection].
   * @param redirectUri The location that your user will be redirected to once the session has been completed successfully.
   * @param expiresIn The number of seconds the Passwordless Session should live before expiring. This value must be between 300 (5 minutes) and 1800 (30 minutes), inclusive.
   * @param state Optional parameter that a Developer can choose to include in their authorization URL.
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class CreateSessionOptions @JvmOverloads constructor(
    val email: String? = null,

    val type: SessionType? = SessionType.MagicLink,

    val connection: String? = null,

    @JsonProperty("redirect_uri")
    val redirectUri: String? = null,

    @JsonProperty("expires_in")
    val expiresIn: Int? = null,

    val state: String? = null
  ) {
    init {
      if (email.isNullOrBlank()) {
        throw IllegalArgumentException("email is required")
      }

      if (expiresIn != null && (expiresIn < 300 || expiresIn > 1800)) {
        throw IllegalArgumentException("'expiresIn' must be a value between 300 and 1800")
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CreateSessionOptionsBuilder {
        return CreateSessionOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [CreateSessionOptions].
     */
    class CreateSessionOptionsBuilder {
      private var email: String? = null
      private var type: SessionType = SessionType.MagicLink
      private var connection: String? = null
      private var redirectUri: String? = null
      private var expiresIn: Int? = null
      private var state: String? = null

      /**
       * The email of the user to authenticate.
       */
      fun email(value: String) = apply { this.email = value }

      /**
       * The type of passwordless session to create.
       */
      fun type(value: SessionType) = apply { this.type = value }

      /**
       * The unique identifier of the [com.workos.sso.models.Connection] to use.
       */
      fun connection(value: String) = apply { this.connection = value }

      /**
       * Optional parameter that a Developer can choose to include in their authorization URL.
       * If included, it will override the default Redirect URI set in the dashboard.
       * This is the location your user will be redirected to once the session has been completed
       * successfully.
       */
      fun redirectUri(value: String) = apply { this.redirectUri = value }

      /**
       * The number of seconds the Passwordless Session should live before expiring.
       * This value must be between 300 (5 minutes) and 1800 (30 minutes), inclusive.
       */
      fun expiresIn(value: Int) = apply { this.expiresIn = value }

      /**
       * Optional parameter that a Developer can choose to include in their authorization URL.
       * If included, then the redirect URI received from WorkOS will contain the exact state
       * that was passed in the authorization URL.
       *
       * The state parameter can be used to encode arbitrary information to help restore application
       * state between redirects.
       */
      fun state(value: String) = apply { this.state = value }

      /**
       * Creates an instance of [CreateSessionOptions] with the given builder parameters.
       */
      fun build(): CreateSessionOptions {
        return CreateSessionOptions(
          email = email,
          type = type,
          connection = connection,
          redirectUri = redirectUri,
          expiresIn = expiresIn,
          state = state
        )
      }
    }
  }
}
