package com.workos.test.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.test.TestBase
import com.workos.users.UsersApi
import com.workos.users.models.User
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersTest : TestBase() {
  private val mapper = jacksonObjectMapper()

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
}
