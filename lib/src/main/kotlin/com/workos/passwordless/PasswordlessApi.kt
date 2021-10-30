package com.workos.passwordless

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.passwordless.models.Session
import com.workos.passwordless.models.SessionType
import java.lang.IllegalArgumentException

class PasswordlessApi(private val workos: WorkOS) {
  fun createSession(createSessionOptions: CreateSessionOptions): Session {
    val config = RequestConfig
      .builder()
      .data(createSessionOptions)
      .build()

    return workos.post(
      "/passwordless/sessions",
      Session::class.java,
      config
    )
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  class CreateSessionOptions @JvmOverloads constructor(
    val email: String?,

    val type: SessionType? = SessionType.MagicLink,

    val connection: String? = null,

    @JsonProperty("redirect_uri")
    val redirectUri: String? = null,
  ) {

    companion object {
      @JvmStatic
      fun builder(): Builder {
        return Builder()
      }
    }

    class Builder {
      private var email: String? = null
      private var type: SessionType = SessionType.MagicLink
      private var connection: String? = null
      private var redirectUri: String? = null

      fun email(value: String) = apply { this.email = value }
      fun type(value: SessionType) = apply { this.type = value }
      fun connection(value: String) = apply { this.connection = value }
      fun redirectUri(value: String) = apply { this.redirectUri = value }

      fun build(): CreateSessionOptions {
        if (email.isNullOrBlank()) {
          throw IllegalArgumentException("email is required")
        }

        return CreateSessionOptions(
          email = email,
          type = type,
          connection = connection,
          redirectUri = redirectUri
        )
      }
    }
  }
}
