package com.workos.test.usermanagement

import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class UserManagementApiTest : TestBase() {
  @Test
  fun getAuthorizationUrlShouldReturnValidUrl() {
    val workos = createWorkOSClient()

    val url = workos.usermanagement.getAuthorizationUrl("client_id", "http://localhost:8080/redirect").build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/user_management/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldAcceptAdditionalParams() {
    val workos = createWorkOSClient()

    val url = workos.usermanagement.getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
      .connectionId("connection_value")
      .provider("provider_value")
      .state("state_value")
      .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/user_management/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&connection_id=connection_value&provider=provider_value&state=state_value",
      url
    )
  }
}
