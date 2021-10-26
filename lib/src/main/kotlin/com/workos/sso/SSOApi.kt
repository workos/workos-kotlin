package com.workos.sso

import com.workos.WorkOS
import com.workos.sso.models.Connection

class SSOApi(val workos: WorkOS) {
    fun deleteConnection() {}

    fun getAuthorizationURL() {}

    fun getConnection(id: String): Connection {
        return workos.get("/connections/$id", Connection::class.java)
    }

    fun getProfileAndToken() {}

    fun getProfile() {}

    fun listConnections() {}
}
