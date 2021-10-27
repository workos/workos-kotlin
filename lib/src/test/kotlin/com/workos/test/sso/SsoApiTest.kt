package com.workos.test.sso

import com.workos.common.exceptions.UnauthorizedException
import com.workos.sso.SsoApi
import com.workos.sso.models.ConnectionType
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(ConnectionType.GoogleOAuth, connection.connectionType)
    }

    @Test
    fun getProfileAndTokenShouldReturnPayload() {
        val workos = createWorkOSClient()

        stubResponse(
            "/sso/token",
            """{
                "token": "01DMEK0J53CVMC32CK5SE0KZ8Q",
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
                    "raw_attributes": {"foo": "bar"}
                }
            }"""
        )

        val profileAndToken = workos.sso.getProfileAndToken("code", "clientId")

        assertEquals("01DMEK0J53CVMC32CK5SE0KZ8Q", profileAndToken.token)
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
                "object": "profile",
                "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
                "raw_attributes": {"foo": "foo_value"}
            }"""
        )

        val profile = workos.sso.getProfile("accessToken")

        assertEquals("prof_01DMC79VCBZ0NY2099737PSVF2", profile.id)
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
                    "connection_type": "GoogleOAuth",
                    "created_at": "2021-10-26 13:29:47.133382",
                    "domains": [],
                    "id": "connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B",
                    "name": "Google OAuth 2.0",
                    "object": "connection",
                    "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
                    "state": "active",
                    "updated_at": "2021-10-26 13:29:47.133382"
                }
            ],
            "listMetadata": {
                "after": null,
                "before": "connection_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
            }
        }"""
        )

        val (connections) = workos.sso.listConnections(SsoApi.ListConnectionsOptions.builder().build())

        assertEquals("connection_01FJYCNTBC2ZTKT4CS1BX0WJ2B", connections.get(0).id)
    }
}
