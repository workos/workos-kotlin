package com.workos.test.passwordless

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.passwordless.PasswordlessApi
import com.workos.passwordless.models.PasswordlessSession
import com.workos.passwordless.models.SessionType
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordlessApiTest : TestBase() {
  private val mapper = jacksonObjectMapper()

  private fun prepareTest(email: String, requestBody: String): PasswordlessSession {
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

    return mapper.readValue(responseBody, PasswordlessSession::class.java)
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

    val email = "marcelina1@foo-corp.com"
    prepareTest(
      email,
      """{
        "email": "$email",
        "type": "MagicLink",
        "redirect_uri": "http://www.google.com"
      }"""
    )

    val createSessionOptions = PasswordlessApi.CreateSessionOptions
      .builder()
      .email(email)
      .redirectUri("http://www.google.com")
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
        "redirect_uri": "/redirect/to",
        "expires_in": 500,
        "state": "app state"
      }"""
    )

    val createSessionOptions = PasswordlessApi.CreateSessionOptions(
      email = email,
      type = SessionType.MagicLink,
      connection = "connectionId",
      redirectUri = "/redirect/to",
      expiresIn = 500,
      state = "app state"
    )

    val session = workos.passwordless.createSession(createSessionOptions)
    assertEquals(session.email, email)
  }

  @Test
  fun createSessionOptionsBuilderWithNoEmailShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions
        .builder()
        .expiresIn(400)
        .build()
    }
  }

  @Test
  fun createSessionOptionsDirectWithNoEmailShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions(expiresIn = 400)
    }
  }

  @Test
  fun createSessionOptionsBuilderWithBelowThresholdExpiresInShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions
        .builder()
        .email("foo@bar.com")
        .expiresIn(299)
        .build()
    }
  }

  @Test
  fun createSessionOptionsDirectWithBelowThresholdShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions(email = "foo@bar.com", expiresIn = 299)
    }
  }

  @Test
  fun createSessionOptionsBuilderWithAboveThresholdExpiresInShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions
        .builder()
        .email("foo@bar.com")
        .expiresIn(1801)
        .build()
    }
  }

  @Test
  fun createSessionOptionsDirectWithAboveThresholdShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PasswordlessApi.CreateSessionOptions(email = "foo@bar.com", expiresIn = 1801)
    }
  }

  @Test
  fun sendSessionReturnsSuccessResponse() {
    val workos = createWorkOSClient()

    val id = "passwordless_session_01EG1BHJMVYMFBQYZTTC0N73CR"

    stubResponse(
      url = "/passwordless/sessions/$id/send",
      responseBody = """{
          "success": true
        }""""
    )

    val response = workos.passwordless.sendSession(id)
    assertEquals(response.success, true)
  }

  @Test
  fun sendSessioReturnsFalseResponse() {
    val workos = createWorkOSClient()

    val id = "nope"

    stubResponse(
      url = "/passwordless/sessions/$id/send",
      responseBody = """{
          "success": false
        }""""
    )

    val response = workos.passwordless.sendSession(id)
    assertEquals(response.success, false)
  }
}
