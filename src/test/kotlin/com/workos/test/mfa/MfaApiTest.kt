package com.workos.test

import com.workos.WorkOS
import com.workos.common.exceptions.NotFoundException
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertNotNull

class MfaApiTest : TestBase() {
  @Test
  fun deleteFactorShouldNotError() {
    val workos = createWorkOSClient()

    val id = "auth_factor_1234"

    stubResponse(
      "/auth/factorss/$id",
      "{}"
    )

    val response = workos.sso.deleteFactor(id)

    assertEquals(Unit, response)
  }

  @Test
  fun deleteFactorShouldThrowError() {
    val workos = createWorkOSClient()

    val id = "auth_factor_1234"

    stubResponse(
      "/auth/factors/$id",
      "{}",
      401
    )

    assertThrows(UnauthorizedException::class.java) {
      workos.sso.deleteFactor(id)
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

    val factor = workos.sso.getFactor(id)

    assertEquals(id, factor.id)
    assertEquals("generic_otp", factor.type)
  }

  @Test
  fun enrollFactorGeneral() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/sso/token",
      requestBody = """{
        "client_id": "clientId",
        "client_secret": "apiKey",
        "code": "code",
        "grant_type": "authorization_code"
      }""",
      responseBody = """{
        "object": "authentication_factor",
        "id": "auth_factor_1234",
        "created_at": "2022-03-15T20:39:19.892Z",
        "updated_at": "2022-03-15T20:39:19.892Z",
        "type": "generic_otp",
        "environment_id": "environment_1234"
      }"""
    )

    val factor = workos.sso.enrollFactor("generic_otp")

    assertEquals("generic_otp", factor.type)
    assertEquals("auth_factor_1234", factor.id)
  }
}
