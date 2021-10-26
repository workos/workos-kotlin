package com.workos.sso

import com.workos.WorkOS
import com.workos.sso.models.Connection
import org.apache.http.client.utils.URIBuilder

class SSOApi(val workos: WorkOS) {
    class AuthorizationURLBuilder(
        val baseURL: String,
        val clientID: String,
        val redirectURI: String,
        var connection: String? = null,
        var domain: String? = null,
        var provider: String? = null,
        var state: String? = null
    ) {
        fun connection(value: String): AuthorizationURLBuilder {
            connection = value
            return this
        }

        fun domain(value: String): AuthorizationURLBuilder {
            domain = value
            return this
        }

        fun provider(value: String): AuthorizationURLBuilder {
            provider = value
            return this
        }

        fun state(value: String): AuthorizationURLBuilder {
            state = value
            return this
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
        workos.delete("/connections/$id")
    }

    fun getAuthorizationURL(clientID: String, redirectURI: String): AuthorizationURLBuilder {
        return AuthorizationURLBuilder.create(workos.baseURL, clientID, redirectURI)
    }

    fun getConnection(id: String): Connection {
        return workos.get("/connections/$id", Connection::class.java)
    }

    fun getProfileAndToken() {}

    fun getProfile() {}

    fun listConnections() {}
}
