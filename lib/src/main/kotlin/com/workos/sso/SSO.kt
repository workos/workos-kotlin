package com.workos.sso

import com.workos.WorkOS
import com.workos.common.entities.Connection
import org.apache.http.client.utils.URIBuilder

class SSO(val workos: WorkOS) {
    class Noop

    class AuthorizationURLBuilder(
        val baseURL: String,
        val clientID: String,
        val redirectURI: String,
        var connection: String? = null,
        var domain: String? = null,
        var provider: String? = null,
        var state: String? = null
    ) {
        fun connection(value: String) {
            connection = value
        }

        fun domain(value: String) {
            domain = value
        }

        fun provider(value: String) {
            provider = value
        }

        fun state(value: String) {
            state = value
        }

        fun build(): String {
            val uriBuilder = URIBuilder("$baseURL")
                .setPath("sso/authorize")
                .addParameter("client_id", clientID)
                .addParameter("redirect_uri", redirectURI)
                .addParameter("response_type", "code")

            if (connection != null) uriBuilder.addParameter("connection", connection)
            if (domain != null) uriBuilder.addParameter("domain", domain)
            if (provider != null) uriBuilder.addParameter("provider", provider)
            if (state != null) uriBuilder.addParameter("state", state)

            return uriBuilder.toString()
        }

        companion object {
            fun create(baseURL: String, clientID: String, redirectURI: String): AuthorizationURLBuilder {
                return AuthorizationURLBuilder(baseURL, clientID, redirectURI)
            }
        }
    }

    fun deleteConnection(id: String) {
        workos.delete("/conections/$id", Noop::class.java)
    }

    fun getAuthorizationURL(clientID: String, redirectURI: String): AuthorizationURLBuilder {
        return AuthorizationURLBuilder.create(workos.baseURL, clientID, redirectURI)
    }

    fun getConnection(id: String): Connection {
        return workos.get("/conections/$id", Connection::class.java)
    }

    fun getProfileAndToken() {}

    fun getProfile() {}

    fun listConnections() {}
}
