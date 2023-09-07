package com.workos.test.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.workos.test.TestBase
import com.workos.users.UsersApi
import com.workos.users.models.FactorType
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersTest : TestBase() {
  private val mapper = jacksonObjectMapper()

  @Test
  fun addUserToOrganizationShouldReturnUser() {
    val workos = createWorkOSClient()

    val id = "user_123"
    val organizationId = "organization_123"

    stubResponse(
      "/users/user_123/organizations",
      """{
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody = """{
        "organization_id": "organization_123"
      }"""
    )

    val options = UsersApi.AddUserToOrganizationOptions.builder()
      .userId(id)
      .organization(organizationId)
      .build()

    val user = workos.users.addUserToOrganization(options)

    assertEquals(id, user.id)
  }

  @Test
  fun authenticateUserWithCodeReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/authenticate",
      """{
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
        }
      }""",
      requestBody = """{
        "code": "code_123",
        "client_id": "client_123",
        "client_secret": "apiKey",
        "grant_type": "authorization_code"
      }"""
    )

    val options = UsersApi.AuthenticateUserWithCodeOptions.builder()
      .code("code_123")
      .clientId("client_123")
      .build()

    val response = workos.users.authenticateUserWithCode(options)

    assertEquals("marcelina@foo-corp.com", response.user.email)
  }

  @Test
  fun authenticateUserWithTotpReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/authenticate",
      """{
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
        }
      }""",
      requestBody = """{
        "code": "code_123",
        "client_id": "client_123",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:mfa-totp",
        "authentication_challenge_id": "auth_challenge_123",
        "pending_authentication_token": "123"
      }"""
    )

    val options = UsersApi.AuthenticateUserWithTotpOptions.builder()
      .code("code_123")
      .clientId("client_123")
      .authenticationChallengeId("auth_challenge_123")
      .pendingAuthenticationToken("123")
      .build()

    val response = workos.users.authenticateUserWithTotp(options)

    assertEquals("marcelina@foo-corp.com", response.user.email)
  }

  @Test
  fun authenticateUserWithMagicAuthReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/authenticate",
      """{
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
        }
      }""",
      requestBody = """{
        "code": "code_123",
        "user_id": "user_123",
        "client_id": "client_123",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:magic-auth:code"
      }"""
    )

    val options = UsersApi.AuthenticateUserWithMagicAuthOptions.builder()
      .code("code_123")
      .userId("user_123")
      .clientId("client_123")
      .build()

    val response = workos.users.authenticateUserWithMagicAuth(options)

    assertEquals("marcelina@foo-corp.com", response.user.email)
  }

  @Test
  fun authenticateUserWithPasswordReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"

    stubResponse(
      "/users/authenticate",
      """{
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
        }
      }""",
      requestBody = """{
        "email": "marcelina@foo-corp.com",
        "password": "pass_123",
        "client_id": "client_123",
        "client_secret": "apiKey",
        "grant_type": "password"
      }"""
    )

    val options = UsersApi.AuthenticateUserWithPasswordOptions.builder()
      .email(email)
      .password("pass_123")
      .clientId("client_123")
      .build()

    val response = workos.users.authenticateUserWithPassword(options)

    assertEquals(email, response.user.email)
  }
  @Test
  fun resetPasswordShouldReturnUser() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/password_reset",
      """{
        "user": {
          "id": "user_123",
          "email": "marcelina@foo-corp.com",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
       }
      }""",
      requestBody = """{
        "token": "token_123",
        "new_password": "test_123"
      }"""
    )

    val options = UsersApi.ResetPasswordOptions
      .builder()
      .token("token_123")
      .newPassword("test_123")
      .build()

    val user = workos.users.resetPassword(options)

    assertEquals("marcelina@foo-corp.com", user.user.email)
  }

  @Test
  fun createPasswordResetChallengeShouldReturnChallengeResponse() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"

    stubResponse(
      "/users/password_reset_challenge",
      """{
        "token": "token_123",
        "user": {
            "id": "user_123",
            "email": "marcelina@foo-corp.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
        }
    }""",
      requestBody = """{
        "email": "$email",
        "password_reset_url": "passwordreseturl.com"
      }"""
    )

    val createPasswordResetChallengeOptions = UsersApi.CreatePasswordResetChallengeOptions
      .builder()
      .email(email)
      .passwordResetUrl("passwordreseturl.com")
      .build()

    val response = workos.users.createPasswordResetChallenge(createPasswordResetChallengeOptions)

    assertEquals(email, response.user.email)
  }

  @Test
  fun createUserShouldReturnUser() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"

    stubResponse(
      url = "/users",
      responseBody = """{
        "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
     }""",
      requestBody = """{
        "email": "$email",
        "email_verified": false
      }"""
    )

    val createUserOptions = UsersApi.CreateUserOptions
      .builder()
      .email(email)
      .build()

    val user = workos.users.createUser(createUserOptions)
    assertEquals(user.email, email)
  }

  @Test
  fun updateUserShouldReturnUser() {
    val workos = createWorkOSClient()

    val id = "user_123"

    stubResponse(
      url = "/users/$id",
      responseBody = """{
        "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
     }""",
    )

    val updateUserOptions = UsersApi.UpdateUserOptions
      .builder()
      .userId(id)
      .build()

    val user = workos.users.updateUser(updateUserOptions)
    assertEquals(user.id, id)
  }

  @Test
  fun deleteUserFromOrganizationShouldReturnUser() {
    val workos = createWorkOSClient()

    val id = "user_123"
    val organization = "organization_123"

    stubResponse(
      "/users/user_123/organizations/organization_123",
      """{
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val options = UsersApi.RemoveUserFromOrganizationOptions.builder()
      .userId(id)
      .organizationId(organization)
      .build()

    val user = workos.users.removeUserFromOrganization(options)

    assertEquals(id, user.id)
  }

  @Test
  fun getUserShouldReturnUser() {
    val workos = createWorkOSClient()

    val id = "user_123"

    stubResponse(
      "/users/$id",
      """{
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val user = workos.users.getUser(id)

    assertEquals(id, user.id)
  }

  @Test
  fun listUsersShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users",
      """{
        "data": [
          {
            "id": "user_123",
            "email": "marcelina@foo-corp.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "user_234"
        }
      }"""
    )

    val (users) = workos.users.listUsers(UsersApi.ListUsersOptions.builder().build())

    assertEquals("user_123", users.get(0).id)
  }

  @Test
  fun listUsersWithPaginationParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/users",
      params = mapOf("after" to WireMock.equalTo("someAfterId"), "before" to WireMock.equalTo("someBeforeId")),
      responseBody = """{
        "data": [
          {
            "id": "user_123",
            "email": "marcelina@foo-corp.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "user_234"
        }
      }""",
    )

    val options = UsersApi.ListUsersOptions.builder()
      .after("someAfterId")
      .before("someBeforeId")
      .build()

    val (users) = workos.users.listUsers(options)

    assertEquals("user_123", users.get(0).id)
  }

  @Test
  fun listUsersWithOptionalParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/users",
      params = mapOf(
        "organization" to WireMock.equalTo("org_123"),
        "email" to WireMock.equalTo("marcelina@foo-corp.com"),
      ),
      responseBody = """{
        "data": [
          {

            "id": "user_123",
            "email": "marcelina@foo-corp.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "user_234"
        }
      }"""
    )

    val options = UsersApi.ListUsersOptions.builder()
      .email("marcelina@foo-corp.com")
      .organization("org_123")
      .build()

    val (users) = workos.users.listUsers(options)

    assertEquals("user_123", users.get(0).id)
  }

  @Test
  fun verifyEmailCodeShouldReturnUser() {
    val workos = createWorkOSClient()

    val userId = "user_123"

    stubResponse(
      "/users/$userId/verify_email_code",
      """{
        "user": {
          "id": "user_123",
          "email": "marcelina@foo-corp.com",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z",
          "email_verified": true
        }
    }""",
      requestBody = """{
    "code": "code_123"
  }"""
    )

    val options = UsersApi.VerifyEmailCodeOptions.builder()
      .code("code_123")
      .userId(userId)
      .build()

    val verificationResponse = workos.users.verifyEmailCode(options)

    assertEquals("user_123", verificationResponse.user.id)
  }

  @Test
  fun sendVerificationEmailShouldReturnUser() {
    val workos = createWorkOSClient()

    val userId = "user_01E4ZCR3C56J083X43JQXF3JK5"

    stubResponse(
      "/users/$userId/send_verification_email",
      """{
          "id": "$userId",
          "email": "example@foo-corp.com",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        }"""
    )

    val response = workos.users.sendVerificationEmail(userId)

    assertEquals("user_01E4ZCR3C56J083X43JQXF3JK5", response.id)
  }

  @Test
  fun sendMagicAuthCodeShouldReturnMagicAuthChallenge() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"

    stubResponse(
      "/users/magic_auth/send",
      """{
          "id": "user_123",
          "email": "example@foo-corp.com",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        }""",
      requestBody = """{
        "email": "$email"
      }"""
    )

    val options = UsersApi.SendMagicAuthCodeOptions.builder()
      .email(email)
      .build()

    val response = workos.users.sendMagicAuthCode(options)

    assertEquals("user_123", response.id)
  }

  @Test
  fun updateUserPasswordShouldReturnUpdatedUser() {
    val workos = createWorkOSClient()

    val id = "user_123"
    val newPassword = "new_password"

    stubResponse(
      "/users/$id/password",
      """{
            "id": "$id",
            "email": "example@foo-corp.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
        }""",
      requestBody = """{
            "password": "$newPassword"
      }"""
    )

    val options = UsersApi.UpdateUserPasswordOptions.builder()
      .password(newPassword)
      .userId(id)
      .build()

    val updateResponse = workos.users.updateUserPassword(options)

    assertEquals(id, updateResponse.id)
  }

  @Test
  fun deleteUserShouldSendDeleteRequest() {
    val workos = createWorkOSClient()

    val userId = "user_01E4ZCR3C56J083X43JQXF3JK5"

    stubResponse(
      "/users/$userId",
      "",
    )

    workos.users.deleteUser(userId)
  }

  @Test
  fun listAuthFactorsShouldReturnAuthenticationFactorList() {
    val workos = createWorkOSClient()

    val id = "user_123"

    stubResponse(
      "/users/$id/auth/factors",
      """{
  "data": [
    {
      "object": "authentication_factor",
      "id": "auth_factor_01H96FETXENNY99ARX0GRC804C",
      "user_id": "user_01H96FETWYSJMJEGF0Q3ZB272F",
      "type": "totp",
      "totp": {
        "issuer": "Foo Corp",
        "user": "user@foo-corp.com"
      },
      "created_at": "2023-08-31T18:59:57.962Z",
      "updated_at": "2023-08-31T18:59:57.962Z"
    }
  ]
}
""",
    )

    val FactorList = workos.users.listAuthFactors(id)

    assertEquals("auth_factor_01H96FETXENNY99ARX0GRC804C", FactorList.data[0].id)
  }

  @Test
  fun enrollFactoTypeReturnsEnrollFactorResponse() {
    val workos = createWorkOSClient()

    val id = "user_123"

    stubResponse(
      "/users/$id/auth/factors",
      """{
  "authentication_factor": {
    "object": "authentication_factor",
    "id": "auth_factor_01H96FETXENNY99ARX0GRC804C",
    "user_id": "user_01H96FETWYSJMJEGF0Q3ZB272F",
    "type": "totp",
    "totp": {
      "issuer": "Foo Corp",
      "qr_code": "data:image/png;base64,iVBOR...",
      "secret": "OFAFOQAPHR6XMQKAIYMWU72XIE3DGI3P",
      "uri": "otpauth://totp/Foo%20Corp:user@foo-corp.com?secret=OFAFOQAPHR6XMQKAIYMWU72XIE3DGI3P&issuer=Foo%20Corp&algorithm=SHA1&digits=6&period=30",
      "user": "user@foo-corp.com"
    },
    "created_at": "2023-08-31T18:59:57.962Z",
    "updated_at": "2023-08-31T18:59:57.962Z"
  },
  "authentication_challenge": {
    "object": "authentication_challenge",
    "id": "auth_challenge_01H96FETXGTW1QMBSBT2T36PW0",
    "authentication_factor_id": "auth_factor_01H96FETXENNY99ARX0GRC804C",
    "expires_at": "2023-08-31T19:09:57.999Z",
    "created_at": "2023-08-31T18:59:57.962Z",
    "updated_at": "2023-08-31T18:59:57.962Z"
  }
}
""",
      requestBody = """{
        "type": "totp"
      }"""
    )

    val options = UsersApi.EnrollAuthFactorOptions.builder()
      .userId(id)
      .type(FactorType.TOTP)
      .build()

    val response = workos.users.enrollAuthFactor(options)

    assertEquals("auth_factor_01H96FETXENNY99ARX0GRC804C", response.authenticationFactor.id)
  }
}
