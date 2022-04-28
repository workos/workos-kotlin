package com.workos.test.mfa

import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.UnprocessableEntityException
import com.workos.mfa.MfaApi
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MfaApiTest : TestBase() {
  @Test
  fun deleteFactorShouldNotError() {
    val workos = createWorkOSClient()

    val id = "auth_factor_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

    stubResponse(
      "/auth/factors/$id",
      "{}"
    )

    val response = workos.mfa.deleteFactor(id)

    assertEquals(Unit, response)
  }

  @Test
  fun deleteFactorShouldThrowError() {
    val workos = createWorkOSClient()

    val id = "auth_factor_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

    stubResponse(
      "/auth/factors/$id",
      "{}",
      401
    )

    assertThrows(UnauthorizedException::class.java) {
      workos.mfa.deleteFactor(id)
    }
  }

  @Test
  fun getFactorShouldReturnFactor() {
    val workos = createWorkOSClient()

    val id = "auth_factor_1234"

    stubResponse(
      "/auth/factors/$id",
      """{
        "object": "authentication_factor",
        "id": "auth_factor_1234",
        "created_at": "2022-03-15T20:39:19.892Z",
        "updated_at": "2022-03-15T20:39:19.892Z",
        "type": "generic_otp",
        "environment_id": "environment_1234"
      }"""
    )

    val factor = workos.mfa.getFactor(id)

    assertEquals(id, factor.id)
    assertEquals("generic_otp", factor.type)
  }

  @Test
  fun enrollFactorGeneral() {
    val workos = createWorkOSClient()

    stubResponse(
      "/auth/factors/enroll",
      """{
        "object": "authentication_factor",
        "id": "auth_factor_1234",
        "created_at": "2022-03-15T20:39:19.892Z",
        "updated_at": "2022-03-15T20:39:19.892Z",
        "type": "generic_otp",
        "environment_id": "environment_1234"
      }"""
    )

    val options = MfaApi.EnrollFactorOptions.builder().type("generic_otp").build()

    val factor = workos.mfa.enrollFactor(options)

    assertEquals("generic_otp", factor.type)
    assertEquals("auth_factor_1234", factor.id)
  }

  @Test
  fun enrollFactorTotp() {
    val workos = createWorkOSClient()

    stubResponse(
      "/auth/factors/enroll",
      """{
        "object": "authentication_factor",
        "id": "auth_factor_1234",
        "created_at": "2022-03-15T20:39:19.892Z",
        "updated_at": "2022-03-15T20:39:19.892Z",
        "type": "totp",
        "environment_id": "environment_1234",
        "totp": {
          "qr_code": "qr-code-test",
          "secret": "secret-test"
        }
      }"""
    )

    val options = MfaApi.EnrollFactorOptions.builder()
      .type("totp")
      .issuer("WorkOS")
      .user("some_user")
      .build()

    val factor = workos.mfa.enrollFactor(options)

    assertEquals("totp", factor.type)
    assertEquals("auth_factor_1234", factor.id)
    assertNotNull(factor.totp)
  }

  @Test
  fun enrollFactorSms() {
    val workos = createWorkOSClient()

    stubResponse(
      "/auth/factors/enroll",
      """{
        "object": "authentication_factor",
        "id": "auth_factor_1234",
        "created_at": "2022-03-15T20:39:19.892Z",
        "updated_at": "2022-03-15T20:39:19.892Z",
        "type": "sms",
        "environment_id": "environment_1234",
        "sms": {
          "phone_number": "+15555555555"
        }
      }"""
    )

    val options = MfaApi.EnrollFactorOptions.builder()
      .type("sms")
      .phoneNumber("+15555555555")
      .build()

    val factor = workos.mfa.enrollFactor(options)

    assertEquals("sms", factor.type)
    assertEquals("auth_factor_1234", factor.id)
    assertNotNull(factor.sms)
  }

  @Test
  fun challengeFactor() {
    val workos = createWorkOSClient()

    stubResponse(
      "/auth/factors/challenge",
      """{
          "authentication_factor_id": "auth_factor_1234",
          "code": "12345",
          "created_at": "2022-03-15T20:39:19.892Z",
          "expires_at": "2022-03-15T21:39:19.892Z",
          "id": "auth_challenge_1234",
          "object": "authentication_challenge",
          "updated_at": "2022-03-15T20:39:19.892Z"
      }"""
    )

    val options = MfaApi.ChallengeFactorOptions.builder()
      .authenticationFactorId("auth_challenge_1234")
      .build()

    val challengeFactor = workos.mfa.challengeFactor(options)

    assertEquals("12345", challengeFactor.code)
    assertEquals("auth_factor_1234", challengeFactor.authenticationFactorId)
  }

  @Test
  fun challengeFactorWithSmsTemplate() {
    val workos = createWorkOSClient()

    stubResponse(
      "/auth/factors/challenge",
      """{
          "authentication_factor_id": "auth_factor_1234",
          "created_at": "2022-03-15T20:39:19.892Z",
          "expires_at": "2022-03-15T21:39:19.892Z",
          "id": "auth_challenge_1234",
          "object": "authentication_challenge",
          "updated_at": "2022-03-15T20:39:19.892Z"
      }"""
    )

    val options = MfaApi.ChallengeFactorOptions.builder()
      .authenticationFactorId("auth_factor_1234")
      .smsTemplate("sms template here")
      .build()

    val challengeFactor = workos.mfa.challengeFactor(options)

    assertEquals("auth_factor_1234", challengeFactor.authenticationFactorId)
    assertNull(challengeFactor.code)
  }

  @Test
  fun verifyFactor() {
    val workos = createWorkOSClient()

    stubResponse(
      "/auth/factors/verify",
      """{
          "challenge": {
            "authentication_factor_id": "auth_factor_1234",
            "code": "12345",
            "created_at": "2022-03-15T20:39:19.892Z",
            "expires_at": "2022-03-15T21:39:19.892Z",
            "id": "auth_challenge_1234",
            "object": "authentication_challenge",
            "updated_at": "2022-03-15T20:39:19.892Z"
          },
          "valid": true
      }"""
    )

    val options = MfaApi.VerifyFactorOptions.builder()
      .authenticationChallengeId("auth_challenge_1234")
      .code("12345")
      .build()

    val verifyResponse = workos.mfa.verifyFactor(options)

    assertEquals(true, verifyResponse.valid)
  }

  @Test
  fun verifyFactorAlreadyVerified() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/auth/factors/verify",
      responseBody = """{
        "code": "Already verified",
        "message": "Already verified"
      }""",
      responseStatus = 422,
    )

    val options = MfaApi.VerifyFactorOptions.builder()
      .authenticationChallengeId("auth_challenge_1234")
      .code("12345")
      .build()

    assertThrows(UnprocessableEntityException::class.java) {
      workos.mfa.verifyFactor(options)
    }
  }
}
