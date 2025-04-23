package com.workos.test.sso

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.exceptions.UnauthorizedException
import com.workos.sso.SsoApi
import com.workos.sso.models.ConnectionState
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SsoApiTest : TestBase() {
  class ExampleResponseType {
    val message: String = ""
  }

  @Test
  fun deleteConnectionShouldNotError() {
    val workos = createWorkOSClient()

    val id = "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

    stubResponse(
      "/connections/$id",
      "{}"
    )

    val response = workos.sso.deleteConnection(id)

    assertEquals(Unit, response)
  }

  @Test
  fun deleteConnectionShouldThrowError() {
    val workos = createWorkOSClient()

    val id = "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

    stubResponse(
      "/connections/$id",
      "{}",
      401
    )

    assertThrows(UnauthorizedException::class.java) {
      workos.sso.deleteConnection(id)
    }
  }

  @Test
  fun getAuthorizationUrlShouldReturnValidUrl() {
    val workos = createWorkOSClient()

    val url = workos.sso.getAuthorizationUrl("client_id", "http://localhost:8080/redirect").build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldAcceptAdditionalParams() {
    val workos = createWorkOSClient()

    val url = workos.sso.getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
      .connection("connection_value")
      .domain("domain_value")
      .provider("provider_value")
      .state("state_value")
      .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&connection=connection_value&domain=domain_value&provider=provider_value&state=state_value",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldAcceptOrganizationParam() {
    val workos = createWorkOSClient()

    val url = workos.sso.getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
      .organization("organization_id")
      .state("state_value")
      .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&organization=organization_id&state=state_value",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldAcceptDomainHintParam() {
    val workos = createWorkOSClient()

    val url = workos.sso.getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
      .organization("organization_id")
      .domainHint("workos.com")
      .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&domain_hint=workos.com&organization=organization_id",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldAcceptLoginHintParam() {
    val workos = createWorkOSClient()

    val url = workos.sso.getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
      .organization("organization_id")
      .loginHint("foo@workos.com")
      .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/sso/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&login_hint=foo%40workos.com&organization=organization_id",
      url
    )
  }

  @Test
  fun getConnectionShouldReturnConnection() {
    val workos = createWorkOSClient()

    val id = "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B"

    stubResponse(
      "/connections/$id",
      """{
        "connection_type": "GoogleOAuth",
        "created_at": "2021-10-26 13:29:47.133382",
        "domains": [],
        "id": "$id",
        "name": "Google OAuth 2.0",
        "object": "connection",
        "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
        "state": "active",
        "updated_at": "2021-10-26 13:29:47.133382"
      }"""
    )

    val connection = workos.sso.getConnection(id)

    assertEquals(id, connection.id)
    assertEquals("GoogleOAuth", connection.connectionType)
    assertEquals(ConnectionState.Active, connection.state)
  }

  @Test
  fun getProfileAndTokenShouldReturnPayload() {
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
        "access_token": "01DMEK0J53CVMC32CK5SE0KZ8Q",
        "profile": {
          "connection_id": "conn_01E4ZCR3C56J083X43JQXF3JK5",
          "connection_type": "OktaSAML",
          "email": "todd@foo-corp.com",
          "first_name": "Todd",
          "id": "prof_01DMC79VCBZ0NY2099737PSVF1",
          "idp_id": "00u1a0ufowBJlzPlk357",
          "last_name": "Rundgren",
          "role":{"slug":"admin"},
          "object": "profile",
          "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
          "custom_attributes": {"license": "professional"},
          "raw_attributes": {"foo": "bar", "license": "professional"}
        }
      }"""
    )

    val profileAndToken = workos.sso.getProfileAndToken("code", "clientId")

    assertEquals("01DMEK0J53CVMC32CK5SE0KZ8Q", profileAndToken.token)
  }

  @Test
  fun getProfileAndTokenWithGroupsShouldReturnGroups() {
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
        "access_token": "01DMEK0J53CVMC32CK5SE0KZ8Q",
        "profile": {
          "connection_id": "conn_01E4ZCR3C56J083X43JQXF3JK5",
          "connection_type": "OktaSAML",
          "email": "todd@foo-corp.com",
          "first_name": "Todd",
          "id": "prof_01DMC79VCBZ0NY2099737PSVF1",
          "idp_id": "00u1a0ufowBJlzPlk357",
          "last_name": "Rundgren",
          "role":{"slug":"admin"},
          "groups":["Admins", "Developers"],
          "object": "profile",
          "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
          "custom_attributes": {"license": "professional"},
          "raw_attributes": {"foo": "bar", "groups":["Admins", "Developers"], "license": "professional"}
        }
      }"""
    )

    val profileAndToken = workos.sso.getProfileAndToken("code", "clientId")
    val profile = profileAndToken.profile

    assertEquals(listOf("Admins", "Developers"), profile.groups)
  }

  @Test
  fun getProfileAndTokenWithoutGroupsShouldNotReturnGroups() {
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
        "access_token": "01DMEK0J53CVMC32CK5SE0KZ8Q",
        "profile": {
          "connection_id": "conn_01E4ZCR3C56J083X43JQXF3JK5",
          "connection_type": "OktaSAML",
          "email": "todd@foo-corp.com",
          "first_name": "Todd",
          "id": "prof_01DMC79VCBZ0NY2099737PSVF1",
          "idp_id": "00u1a0ufowBJlzPlk357",
          "last_name": "Rundgren",
          "role":{"slug":"admin"},
          "object": "profile",
          "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
          "custom_attributes": {"license": "professional"},
          "raw_attributes": {"foo": "bar", "license": "professional"}
        }
      }"""
    )

    val profileAndToken = workos.sso.getProfileAndToken("code", "clientId")
    val profile = profileAndToken.profile

    assertNull(profile.groups)
  }

  @Test
  fun getProfileAndTokenWithoutRoleShouldNotReturnRole() {
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
        "access_token": "01DMEK0J53CVMC32CK5SE0KZ8Q",
        "profile": {
          "connection_id": "conn_01E4ZCR3C56J083X43JQXF3JK5",
          "connection_type": "OktaSAML",
          "email": "todd@foo-corp.com",
          "first_name": "Todd",
          "id": "prof_01DMC79VCBZ0NY2099737PSVF1",
          "idp_id": "00u1a0ufowBJlzPlk357",
          "last_name": "Rundgren",
          "object": "profile",
          "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
          "custom_attributes": {"license": "professional"},
          "raw_attributes": {"foo": "bar", "license": "professional"}
        }
      }"""
    )

    val profileAndToken = workos.sso.getProfileAndToken("code", "clientId")
    val profile = profileAndToken.profile

    assertNull(profile.role)
  }

  @Test
  fun getProfileShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      "/sso/profile",
      """{
        "connection_id": "conn_01E4ZCR3C56J083X43JQXF3JK5",
        "connection_type": "OktaSAML",
        "email": "todd@foo-corp.com",
        "first_name": "Todd",
        "id": "prof_01DMC79VCBZ0NY2099737PSVF2",
        "idp_id": "00u1a0ufowBJlzPlk357",
        "last_name": "Rundgren",
        "role":{"slug":"admin"},
        "object": "profile",
        "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
        "custom_attributes": {"license": "professional"},
        "raw_attributes": {"foo": "foo_value", "license": "professional"}
      }"""
    )

    val profile = workos.sso.getProfile("accessToken")

    assertEquals("prof_01DMC79VCBZ0NY2099737PSVF2", profile.id)
    assertEquals("professional", profile.customAttributes.get("license"))
    assertEquals("foo_value", profile.rawAttributes.get("foo"))
  }

  @Test
  fun listConnectionsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      "/connections",
      """{
        "data": [
          {
            "connection_type": "GitHubOAuth",
            "created_at": "2021-10-26 13:29:47.133382",
            "domains": [],
            "id": "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B",
            "name": "GitHub OAuth",
            "object": "connection",
            "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
            "state": "active",
            "updated_at": "2021-10-26 13:29:47.133382"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "connection_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
        }
      }"""
    )

    val (connections) = workos.sso.listConnections(SsoApi.ListConnectionsOptions.builder().build())

    assertEquals("connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B", connections.get(0).id)
    assertEquals("GitHubOAuth", connections.get(0).connectionType)
  }

  @Test
  fun listConnectionsWithPaginationParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/connections",
      params = mapOf("after" to equalTo("someAfterId"), "before" to equalTo("someBeforeId")),
      responseBody = """{
        "data": [
          {
            "connection_type": "GoogleOAuth",
            "created_at": "2021-10-26 13:29:47.133382",
            "domains": [],
            "id": "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B",
            "name": "Google OAuth 2.0",
            "object": "connection",
            "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
            "state": "inactive",
            "updated_at": "2021-10-26 13:29:47.133382"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "connection_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
        }
      }"""
    )

    val options = SsoApi.ListConnectionsOptions.builder()
      .after("someAfterId")
      .before("someBeforeId")
      .build()

    val (connections) = workos.sso.listConnections(options)

    assertEquals("connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B", connections.get(0).id)
    assertEquals(ConnectionState.Inactive, connections.get(0).state)
  }

  @Test
  fun listConnectionsWithOptionalParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/connections",
      params = mapOf(
        "connection_type" to equalTo("GoogleSAML"),
        "domain" to equalTo("domain.com"),
        "organization_id" to equalTo("org_123")
      ),
      responseBody = """{
        "data": [
          {
            "connection_type": "GoogleSAML",
            "created_at": "2021-10-26 13:29:47.133382",
            "domains": [],
            "id": "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B",
            "name": "GoogleSAML Connection",
            "object": "connection",
            "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
            "state": "draft",
            "updated_at": "2021-10-26 13:29:47.133382"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "connection_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
        }
      }"""
    )

    val options = SsoApi.ListConnectionsOptions.builder()
      .connectionType("GoogleSAML")
      .domain("domain.com")
      .organizationId("org_123")
      .build()

    val (connections) = workos.sso.listConnections(options)

    assertEquals("connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B", connections.get(0).id)
    assertEquals(ConnectionState.Draft, connections.get(0).state)
  }
}
