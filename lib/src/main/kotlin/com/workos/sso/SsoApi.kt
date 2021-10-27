package com.workos.sso

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.sso.models.Connection
import com.workos.sso.models.ConnectionList
import com.workos.sso.models.ConnectionType
import com.workos.sso.models.Profile
import com.workos.sso.models.ProfileAndToken
import org.apache.http.client.utils.URIBuilder

class SsoApi(val workos: WorkOS) {
  class AuthorizationUrlBuilder(
    val baseUrl: String,
    val clientId: String,
    val redirectUri: String,
    var connection: String? = null,
    var domain: String? = null,
    var provider: String? = null,
    var state: String? = null
  ) {
    fun connection(value: String): AuthorizationUrlBuilder {
      connection = value
      return this
    }

    fun domain(value: String): AuthorizationUrlBuilder {
      domain = value
      return this
    }

    fun provider(value: String): AuthorizationUrlBuilder {
      provider = value
      return this
    }

    fun state(value: String): AuthorizationUrlBuilder {
      state = value
      return this
    }

    fun build(): String {
      val uriBuilder = URIBuilder("$baseUrl")
        .setPath("sso/authorize")
        .addParameter("client_id", clientId)
        .addParameter("redirect_uri", redirectUri)
        .addParameter("response_type", "code")

      if (connection != null) uriBuilder.addParameter("connection", connection)
      if (domain != null) uriBuilder.addParameter("domain", domain)
      if (provider != null) uriBuilder.addParameter("provider", provider)
      if (state != null) uriBuilder.addParameter("state", state)

      return uriBuilder.toString()
    }

    companion object {
      fun create(baseUrl: String, clientId: String, redirectUri: String): AuthorizationUrlBuilder {
        return AuthorizationUrlBuilder(baseUrl, clientId, redirectUri)
      }
    }
  }

  fun deleteConnection(id: String) {
    workos.delete("/connections/$id")
  }

  fun getAuthorizationUrl(clientId: String, redirectUri: String): AuthorizationUrlBuilder {
    return AuthorizationUrlBuilder.create(workos.baseUrl, clientId, redirectUri)
  }

  fun getConnection(id: String): Connection {
    return workos.get("/connections/$id", Connection::class.java)
  }

  fun getProfileAndToken(code: String, clientId: String): ProfileAndToken {
    val params = mapOf<String, String>(
      "client_id" to clientId,
      "client_secret" to workos.apiKey,
      "code" to code,
      "grant_type" to "authorization_code",
    )

    val config = RequestConfig.builder()
      .params(params)
      .build()

    return workos.post("/sso/token", ProfileAndToken::class.java, config)
  }

  fun getProfile(accessToken: String): Profile {
    val headers = mapOf("Authorization" to "Bearer $accessToken")

    val config = RequestConfig.builder()
      .headers(headers)
      .build()

    return workos.get("/sso/profile", Profile::class.java, config)
  }

  class ListConnectionsOptions @JvmOverloads constructor(
    connectionType: ConnectionType? = null,
    domain: String? = null,
    organizationId: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null
  ) : PaginationParams(after, before, limit) {
    init {
      if (connectionType != null) set("connection_type", connectionType.toString())
      if (domain != null) set("domain", domain)
      if (organizationId != null) set("organization_id", organizationId)
    }

    companion object {
      fun builder(): Builder {
        return Builder()
      }
    }

    class Builder : PaginationParams.Builder<ListConnectionsOptions>(ListConnectionsOptions()) {
      fun connectionType(value: ConnectionType) = apply { this.params["connection_type"] = value.toString() }
      fun domain(value: String) = apply { this.params["domain"] = value }
      fun organizationId(value: String) = apply { this.params["organization_id"] = value }
    }
  }

  fun listConnections(options: ListConnectionsOptions): ConnectionList {
    val config = RequestConfig.builder()
      .params(options)
      .build()

    return workos.get("/connections", ConnectionList::class.java, config)
  }
}
