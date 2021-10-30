package com.workos.test.passwordless

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.passwordless.PasswordlessApi
import com.workos.passwordless.models.Session
import com.workos.passwordless.models.SessionType
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordlessApiTest : TestBase() {
  private val mapper = jacksonObjectMapper()

  private fun prepareTest(email: String, requestBody: String): Session {
    val responseBody = """{
        "id": "passwordless_session_01EHDAK2BFGWCSZXP9HGZ3VK8C",
        "email": "$email",
        "expires_at": "2020-08-13T05:50:00.000Z",
        "link": "https://auth.workos.com/passwordless/token/confirm",
        "object": "passwordless_session"
      }"""

    stubResponse(
      url = "/passwordless/sessions",
      responseBody = responseBody,
      requestBody = requestBody
    )

    return mapper.readValue(responseBody, Session::class.java)
  }

  @Test
  fun createSessionWithEmailShouldReturnSession() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"
    prepareTest(
      email,
      """{
        "email": "$email",
        "type": "MagicLink"
      }"""
    )

    val createSessionOptions = PasswordlessApi.CreateSessionOptions
      .builder()
      .email(email)
      .build()

    val session = workos.passwordless.createSession(createSessionOptions)
    assertEquals(session.email, email)
  }

  @Test
  fun createSessionWithEmailAndTypeShouldReturnSession() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"
    prepareTest(
      email,
      """{
        "email": "$email",
        "type": "MagicLink"
      }"""
    )

    val createSessionOptions = PasswordlessApi.CreateSessionOptions
      .builder()
      .email(email)
      .type(SessionType.MagicLink)
      .build()

    val session = workos.passwordless.createSession(createSessionOptions)
    assertEquals(session.email, email)
  }

  @Test
  fun createSessionWithAllParamsShouldReturnSession() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"
    prepareTest(
      email,
      """{
        "email": "$email",
        "type": "MagicLink",
        "connection": "connectionId",
        "redirect_uri": "/redirect/to"
      }"""
    )

    val createSessionOptions = PasswordlessApi.CreateSessionOptions(
      email = email,
      type = SessionType.MagicLink,
      connection = "connectionId",
      redirectUri = "/redirect/to"
    )

    val session = workos.passwordless.createSession(createSessionOptions)
    assertEquals(session.email, email)
  }

  @Test
  fun createSessionOptionsBuilderWithNoEmailShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions
        .builder()
        .build()
    }
  }
}
