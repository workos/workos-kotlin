package com.workos.test.widgets

import com.workos.test.TestBase
import com.workos.widgets.builders.GetTokenOptionsBuilder
import com.workos.widgets.models.WidgetScope
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetsApiTest : TestBase() {
  private fun prepareGetTokenTest(body: String): String {
    val token = "abc123456"

    stubResponse(
      url = "/widgets/token",
      responseBody = """{
          "token": "$token"
        }""",
      requestBody = body
    )

    return token
  }

  @Test
  fun getTokenShouldReturnPayload() {
    val workos = createWorkOSClient()

    val token = prepareGetTokenTest(
      """{
        "organization_id": "organizationId",
        "user_id": "userId",
        "scopes": ["widgets:users-table:manage"]
      }"""
    )

    val options = GetTokenOptionsBuilder("organizationId", "userId", listOf(WidgetScope.UsersTableManagement))
      .build()

    val response = workos.widgets.getToken(options)

    assertEquals(response.token, token)
  }
}
