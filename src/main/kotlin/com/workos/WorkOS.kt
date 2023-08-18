package com.workos

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.workos.auditlogs.AuditLogsApi
import com.workos.common.exceptions.BadRequestException
import com.workos.common.exceptions.GenericServerException
import com.workos.common.exceptions.NotFoundException
import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.UnprocessableEntityException
import com.workos.common.http.BadRequestExceptionResponse
import com.workos.common.http.GenericErrorResponse
import com.workos.common.http.RequestConfig
import com.workos.common.http.UnprocessableEntityExceptionResponse
import com.workos.directorysync.DirectorySyncApi
import com.workos.mfa.MfaApi
import com.workos.organizations.OrganizationsApi
import com.workos.passwordless.PasswordlessApi
import com.workos.portal.PortalApi
import com.workos.sso.SsoApi
import com.workos.users.UsersApi
import com.workos.webhooks.WebhooksApi
import org.apache.http.client.utils.URIBuilder
import java.lang.IllegalArgumentException
import java.util.Properties

/**
 * Global configuration class for interacting with the WorkOS API.
 *
 * @param apiKey The API Key used for authenticating requests.
 */
class WorkOS(
  val apiKey: String
) {
  /**
   * Allows APIKey to be used in other packages as needed.
   */
  init {
    Companion.apiKey = apiKey
  }

  companion object {
    lateinit var apiKey: String
    private set
  }

  /**
   * Host to send requests to.
   */
  @JvmField
  var apiHostname = "api.workos.com"

  /**
   * Whether or not to use HTTPS for requests.
   */
  @JvmField
  var https: Boolean = true

  /**
   * The port to send requests to.
   */
  @JvmField
  var port: Int? = null

  /**
   * Module for interacting with the Audit Logs API.
   */
  @JvmField
  val auditLogs = AuditLogsApi(this)

  /**
   * Module for interacting with the Directory Sync API.
   */
  @JvmField
  val directorySync = DirectorySyncApi(this)

  /**
   * Module for interacting with the Organizations API.
   */
  @JvmField
  val organizations = OrganizationsApi(this)

  /**
   * Module for interacting with Passwordless Sessions API.
   */
  @JvmField
  val passwordless = PasswordlessApi(this)

  /**
   * Module for interacting with the Admin Portal API.
   */
  @JvmField
  val portal = PortalApi(this)

  /**
   * Module for interacting with the Single Sign On API.
   */
  @JvmField
  val sso = SsoApi(this)

  /**
   * Module for interacting with the Single Sign On API.
   */
  @JvmField
  val mfa = MfaApi(this)

  /**
   * Module for interacting with the Users API.
   */
  @JvmField
  val users = UsersApi(this)

  /**
   * Module for interacting with the Webhooks API.
   */
  @JvmField
  val webhooks = WebhooksApi()

  /**
   * The base URL for making API requests to.
   */
  val baseUrl: String
    get() {
      val url = "$protocol://$apiHostname"
      return if (port == null) url else "$url:$port"
    }

  private val versionProperties = Properties()

  /**
   * WorkOS SDK Version
   */
  val version: String
    get() {
      return versionProperties.getProperty("version") ?: "unknown"
    }

  private val protocol: String
    get() {
      return if (https) "https" else "http"
    }

  private val manager = FuelManager()

  private val mapper = jacksonMapperBuilder()
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    .build()

  init {
    if (apiKey.isNullOrBlank()) {
      throw IllegalArgumentException("Missing API key")
    }

    val versionPropertiesFile = this.javaClass.getResourceAsStream("/version.properties")
    versionProperties.load(versionPropertiesFile)

    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    manager.removeAllResponseInterceptors()
    manager.basePath = baseUrl
    manager.baseHeaders = mapOf(
      "Authorization" to "Bearer $apiKey",
      "User-Agent" to "workos-kotlin/$version",
      "Content-Type" to "application/json"
    )
  }

  /**
   * Performs a GET request with the baseURL prepended to the given path.
   */
  fun <Res : Any> get(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path)

    if (config?.params != null) {
      for ((key, value) in config.params.entries) {
        uri.addParameter(key, value)
      }
    }

    val request = manager.get(uri.toString())

    return sendRequest(buildRequest(request, config), responseType)
  }

  /**
   * Performs a POST request with WorkOS configuration parameters.
   */
  fun post(path: String, config: RequestConfig? = null) {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val request = manager.post(uri.toString()).body(body)

    sendRequest(buildRequest(request, config))
  }

  /**
   * Performs a POST request with WorkOS configuration parameters.
   */
  fun <Res : Any> post(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val request = manager.post(uri.toString()).body(body)

    return sendRequest(buildRequest(request, config), responseType)
  }

  /**
   * Performs a PUT request with WorkOS configuration parameters.
   */
  fun <Res : Any> put(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val request = manager.put(uri.toString()).body(body)

    return sendRequest(buildRequest(request, config), responseType)
  }

  /**
   * Performs a DELETE request with WorkOS configuration parameters.
   */
  fun delete(path: String, config: RequestConfig? = null): String {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val request = manager.delete(uri.toString())

    return sendRequest(buildRequest(request, config))
  }

  private fun buildRequest(request: Request, config: RequestConfig? = null): Request {
    if (config?.headers != null) {
      for ((key, value) in config.headers) {
        request.set(key, value)
      }
    }
    return request
  }

  private fun sendRequest(request: Request): String {
    val (_, response) = request.responseString()

    var payload = String(response.data)

    if (payload.isEmpty()) {
      payload = "{}"
    }

    if (response.statusCode >= 400) {
      handleResponseError(response, payload)
    }

    return payload
  }

  private fun <Res : Any> sendRequest(request: Request, responseType: Class<Res>): Res {
    val response = sendRequest(request)
    return mapper.readValue(response, responseType)
  }

  private fun handleResponseError(response: Response, payload: String) {
    val requestId = response.header("X-Request-ID").first()

    when (val status = response.statusCode) {
      400 -> {
        val responseData = mapper.readValue(payload, BadRequestExceptionResponse::class.java)
        throw BadRequestException(responseData.message, responseData.code, responseData.errors, requestId)
      }
      401 -> {
        val responseData = mapper.readValue(payload, GenericErrorResponse::class.java)
        throw UnauthorizedException(responseData.message, requestId)
      }
      404 -> {
        throw NotFoundException(response.url.path, requestId)
      }
      422 -> {
        val unprocessableEntityException = mapper.readValue(payload, UnprocessableEntityExceptionResponse::class.java)
        throw UnprocessableEntityException(unprocessableEntityException.message, unprocessableEntityException.code, unprocessableEntityException.errors, requestId)
      }
      else -> {
        val responseData = mapper.readValue(payload, GenericErrorResponse::class.java)
        throw GenericServerException(responseData.message, status, requestId)
      }
    }
  }
}
