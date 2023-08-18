package com.workos.test.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.workos.sso.models.ConnectionState
import com.workos.sso.models.ConnectionType
import com.workos.test.TestBase
import com.workos.users.UsersApi
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersTest : TestBase() {
  private val mapper = jacksonObjectMapper()

  @Test
  fun addUserToOrganizationShouldReturnUser() {
    val workos = createWorkOSClient()

    val id = "user_123"
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

    val options = UsersApi.AddUserToOrganizationOptions.builder()
      .id(id)
      .organization(organization)
      .build()

    val user = workos.users.addUserToOrganization(options)

    assertEquals(id, user.id)
  }

  @Test
  fun authenticateUserWithPasswordReturnsAuthenticationResponse() {
    val workos = createWorkOSClient()

    val email = "marcelina@foo-corp.com"

    stubResponse(
      "/users/sessions/token",
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
      }"""
    )

    val options = UsersApi.AuthenticateUserWithPasswordOptions.builder()
      .email(email)
      .password("pass_123")
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
  fun createPasswordResetChallengeShouldReturnPasswordResetChallengeResponse() {
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

    val id = "user_123"
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
      .id(id)
      .organization(organization)
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

  @Test
  fun verifySessionShouldReturnVerifySessionResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/users/sessions/verify",
      """{
        "user": {
          "id": "user_123",
          "email": "marcelina@foo-corp.com",
          "user_type": "unmanaged",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
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
      }
}"""

    )

    val options = UsersApi.VerifySessionOptions.builder()
      .token("token_123")
      .clientID("client_123")
      .build()

    val response = workos.users.verifySession(options)

    assertEquals("marcelina@foo-corp.com", response.user.email)
    assertEquals("sample_id_12345", response.session.id)
  }
}

