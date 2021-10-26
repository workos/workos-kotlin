package com.workos.sso

import com.workos.sso.models.ConnectionType
import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class SSOTest : TestBase() {
    class ExampleResponseType {
        val message: String = ""
    }

    class ExampleRequestEntity {
        val param: String = ""
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
            "organization_id": "org_01FJYCNTB6VC4K5R8BTF86286Q",
            "state": "active",
            "updated_at": "2021-10-26 13:29:47.133382"
        }"""
        )

        val connection = workos.sso.getConnection(id)

        assertEquals(id, connection.id)
        assertEquals(ConnectionType.GoogleOAuth, connection.connectionType)
    }
}
