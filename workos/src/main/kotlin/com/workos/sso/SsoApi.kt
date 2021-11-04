package com.workos.sso

import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.sso.models.Connection
import com.workos.sso.models.ConnectionList
import com.workos.sso.models.ConnectionType
import com.workos.sso.models.Profile
import com.workos.sso.models.ProfileAndToken
import org.apache.http.client.utils.URIBuilder

/**
 * The SsoApi class provides convenience methods for working with the WorkOS
 * SSO platform. You'll need a valid API key, a client ID, and to have
 * created an SSO connection on your WorkOS dashboard.
 */
class SsoApi(private val workos: WorkOS) {
  /**
   * Builder for authorization URLs. Can be created via `getAuthorizationUrl` method.
   */
  class AuthorizationUrlOptions @JvmOverloads constructor(
    val baseUrl: String,
    val clientId: String,
    val redirectUri: String,
    var connection: String? = null,
    var domain: String? = null,
    var provider: String? = null,
    var state: String? = null
  ) {
    fun connection(value: String) = apply { connection = value }
    fun domain(value: String) = apply { domain = value }
    fun provider(value: String) = apply { provider = value }
    fun state(value: String) = apply { state = value }

    fun build(): String {
      val uriBuilder = URIBuilder("$baseUrl")
        .setPath("/sso/authorize")
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
      @JvmStatic
      fun builder(baseUrl: String, clientId: String, redirectUri: String): AuthorizationUrlOptions {
        return AuthorizationUrlOptions(baseUrl, clientId, redirectUri)
      }
    }
  }

  /**
   * Deletes a single connection by id.
   */
  fun deleteConnection(id: String) {
    workos.delete("/connections/$id")
  }

  /**
   * Generate an Oauth2 authorization URL where your users will
   * authenticate using the configured SSO Identity Provider.
   */
  fun getAuthorizationUrl(clientId: String, redirectUri: String): AuthorizationUrlOptions {
    return AuthorizationUrlOptions.builder(workos.baseUrl, clientId, redirectUri)
  }

  /**
   * Fetches a single connection by id.
   */
  fun getConnection(id: String): Connection {
    return workos.get("/connections/$id", Connection::class.java)
  }

  private class ProfileAndTokenOptions(
    val code: String,

    @JsonProperty("client_id")
    val clientId: String,

    @JsonProperty("client_secret")
    val clientSecret: String
  ) {
    @JsonProperty("grant_type")
    val grantType = "authorization_code"
  }

  /**
   * Fetch the profile details and access token for the authenticated SSO user.
   */
  fun getProfileAndToken(code: String, clientId: String): ProfileAndToken {
    val config = RequestConfig.builder()
      .data(ProfileAndTokenOptions(code, clientId, workos.apiKey))
      .build()

    return workos.post("/sso/token", ProfileAndToken::class.java, config)
  }

  /**
   * Fetch the profile details via provided access token.
   */
  fun getProfile(accessToken: String): Profile {
    val headers = mapOf("Authorization" to "Bearer $accessToken")

    val config = RequestConfig.builder()
      .headers(headers)
      .build()

    return workos.get("/sso/profile", Profile::class.java, config)
  }

  /**
   * Options builder for `listConnections` method.
   */
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

  /**
   * Fetches list of connections.
   */
  @JvmOverloads
  fun listConnections(options: ListConnectionsOptions = ListConnectionsOptions()): ConnectionList {
    val config = RequestConfig.builder()
      .params(options)
      .build()

    return workos.get("/connections", ConnectionList::class.java, config)
  }
}
