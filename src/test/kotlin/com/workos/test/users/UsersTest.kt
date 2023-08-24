package com.workos.test.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.workos.test.TestBase
import com.workos.users.UsersApi
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersTest : TestBase() {
  private val mapper = jacksonObjectMapper()

  @Test
  fun addUserToOrganizationShouldReturnUser() {
    val workos = createWorkOSClient()

    val userId = "user_123"
    val organizationId = "organization_123"

    stubResponse(
      "/users/user_123/organizations/organization_123",
      """{
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "unmanaged",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val options = UsersApi.AddUserToOrganizationOptions.builder()
      .user(userId)
      .organization(organizationId)
      .build()

    val user = workos.users.addUserToOrganization(options)

    assertEquals(userId, user.id)
  }

  @Test
  fun authenticateUserWithCodeReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/authentications",
      """{
        "session": {
          "id": "sample_id_12345",
          "token": "token_123",
          "created_at": "2023-07-15T19:07:33.155Z",
          "expires_at": "2023-08-15T19:07:33.155Z",
          "authorized_organizations": [{
              "organization": {
                  "name": "OrgName",
                  "id": "Org123"
              }
          }]
        },
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "managed",
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
  fun authenticateUserWithMagicAuthReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/authentications",
      """{
        "session": {
          "id": "sample_id_12345",
          "token": "token_123",
          "created_at": "2023-07-15T19:07:33.155Z",
          "expires_at": "2023-08-15T19:07:33.155Z",
          "authorized_organizations": [{
              "organization": {
                  "name": "OrgName",
                  "id": "Org123"
              }
          }]
        },
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "unmanaged",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
        }
      }""",
      requestBody = """{
        "code": "code_123",
        "magic_auth_challenge_id": "challenge_123",
        "client_id": "client_123",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:magic-auth:code"
      }"""
    )

    val options = UsersApi.AuthenticateUserWithMagicAuthOptions.builder()
      .code("code_123")
      .magicAuthChallengeId("challenge_123")
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
      "/users/authentications",
      """{
        "session": {
          "id": "sample_id_12345",
          "token": "token_123",
          "created_at": "2023-07-15T19:07:33.155Z",
          "expires_at": "2023-08-15T19:07:33.155Z",
          "authorized_organizations": [{
              "organization": {
                  "name": "OrgName",
                  "id": "Org123"
              }
          }]
        },
        "user": {
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "unmanaged",
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
  fun completePasswordResetShouldReturnUser() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/password_reset",
      """{
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "unmanaged",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val completePasswordResetOptions = UsersApi.CompletePasswordResetOptions
      .builder()
      .token("token_123")
      .newPassword("test_123")
      .build()

    val user = workos.users.completePasswordReset(completePasswordResetOptions)

    assertEquals("marcelina@foo-corp.com", user.email)
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
            "user_type": "unmanaged",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
        }
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

    val responseBody = """{
        "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "unmanaged",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
     }"""

    stubResponse(
      url = "/users",
      responseBody = responseBody,
    )

    val email = "marcelina@foo-corp.com"

    val createUserOptions = UsersApi.CreateUserOptions
      .builder()
      .email(email)
      .build()

    val user = workos.users.createUser(createUserOptions)
    assertEquals(user.email, email)
  }

  @Test
  fun deleteUserFromOrganizationShouldReturnUser() {
    val workos = createWorkOSClient()

    val userId = "user_123"
    val organization = "organization_123"

    stubResponse(
      "/users/user_123/organizations/organization_123",
      """{
         "id": "user_123",
        "email": "marcelina@foo-corp.com",
        "user_type": "unmanaged",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val options = UsersApi.RemoveUserFromOrganizationOptions.builder()
      .user(userId)
      .organization(organization)
      .build()

    val user = workos.users.removeUserFromOrganization(options)

    assertEquals(userId, user.id)
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
        "user_type": "unmanaged",
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
            "user_type": "unmanaged",
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
            "user_type": "unmanaged",
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
            "user_type": "unmanaged",
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

  fun verifyEmailShouldReturnUser() {
    val workos = createWorkOSClient()

    val magicAuthChallengeId = "auth_challenge_123"
    val code = "123456"

    stubResponse(
      "/users/verify_email",
      """{
            "id": "user_123",
            "email": "marcelina@foo-corp.com",
            "user_type": "unmanaged",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
        }""",
      requestBody = """{
            "magic_auth_challenge_id": "auth_challenge_123",
            "code": "123456"
      }"""
    )

    val options = UsersApi.VerifyEmailOptions.builder()
      .magicAuthChallengeId(magicAuthChallengeId)
      .code(code)
      .build()

    val verificationResponse = workos.users.verifyEmail(options)

    assertEquals("user_123", verificationResponse.id)
  }

  @Test
  fun sendVerificationEmailShouldReturnUser() {
    val workos = createWorkOSClient()

    val userId = "user_01E4ZCR3C56J083X43JQXF3JK5"

    stubResponse(
      "/users/$userId/send_verification_email",
      """{
           "id": "magic_auth_challenge_123"
        }""",
      requestBody = """{
        "user_id": "$userId"
      }"""
    )

    val options = UsersApi.SendVerificationEmailOptions.builder()
      .userId(userId)
      .build()

    val response = workos.users.sendVerificationEmail(options)

    assertEquals("magic_auth_challenge_123", response.id)
  }

  @Test
  fun sendMagicAuthCodeShouldReturnMagicAuthChallenge() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"

    stubResponse(
      "/users/magic_auth/send",
      """{
           "id": "magic_auth_challenge_123"
        }""",
      requestBody = """{
        "email": "$email"
      }"""
    )

    val options = UsersApi.SendMagicAuthCodeOptions.builder()
      .email(email)
      .build()

    val response = workos.users.sendMagicAuthCode(options)

    assertEquals("magic_auth_challenge_123", response.id)
  }
}
