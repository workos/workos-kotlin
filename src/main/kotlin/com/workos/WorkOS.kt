package com.workos

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.common.exceptions.GenericServerException
import com.workos.common.exceptions.NotFoundException
import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.UnprocessableEntityException
import com.workos.common.http.GenericErrorResponse
import com.workos.common.http.RequestConfig
import com.workos.common.http.UnprocessableEntityExceptionResponse
import com.workos.directorysync.DirectorySyncApi
import com.workos.organizations.OrganizationsApi
import com.workos.passwordless.PasswordlessApi
import com.workos.portal.PortalApi
import com.workos.sso.SsoApi
import com.workos.webhooks.WebhooksApi
import org.apache.http.client.utils.URIBuilder
import java.lang.IllegalArgumentException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

/**
 * Global configuration class for interacting with the WorkOS API.
 *
 * @param apiKey The API Key used for authenticating requests.
 */
class WorkOS(
  val apiKey: String
) {

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

  private var version: String = "1.0.0-beta-2"

  private val httpClient = HttpClient.newBuilder().build()

  private val protocol: String
    get() {
      return if (https) "https" else "http"
    }

  private val requestBuilder =
    HttpRequest.newBuilder()
      .header("Authorization", "Bearer $apiKey")
      .header("User-Agent", "workos-kotlin/$version")
      .header("Content-Type", "application/json")

  private val mapper = jacksonObjectMapper()

  init {
    if (apiKey.isNullOrBlank()) {
      throw IllegalArgumentException("Missing API key")
    }
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
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

    val requestBuilder = requestBuilder.copy().GET().uri(uri.build())

    return sendRequest(buildRequest(requestBuilder, config), responseType)
  }

  /**
   * Performs a POST request with WorkOS configuration parameters.
   */
  fun <Res : Any> post(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val requestBuilder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

    return sendRequest(buildRequest(requestBuilder, config), responseType)
  }

  /**
   * Performs a PUT request with WorkOS configuration parameters.
   */
  fun <Res : Any> put(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val requestBuilder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

    return sendRequest(buildRequest(requestBuilder, config), responseType)
  }

  /**
   * Performs a DELETE request with WorkOS configuration parameters.
   */
  fun delete(path: String, config: RequestConfig? = null): HttpResponse<String> {
    val uri = URIBuilder(baseUrl).setPath(path).build()
    val requestBuilder = requestBuilder.copy().DELETE().uri(uri)
    return sendRequest(buildRequest(requestBuilder, config))
  }

  private fun buildRequest(requestBuilder: HttpRequest.Builder, config: RequestConfig? = null): HttpRequest {
    if (config?.headers != null) {
      for ((key, value) in config.headers) {
        requestBuilder.setHeader(key, value)
      }
    }
    return requestBuilder.build()
  }

  private fun sendRequest(request: HttpRequest): HttpResponse<String> {
    val response = httpClient.send(request, BodyHandlers.ofString())

    if (response.statusCode() >= 400) {
      handleResponseError(response)
    }

    return response
  }

  private fun <Res : Any> sendRequest(request: HttpRequest, responseType: Class<Res>): Res {
    val response = sendRequest(request)
    return mapper.readValue(response.body(), responseType)
  }

  private fun handleResponseError(response: HttpResponse<String>) {
    val requestId = response.headers().firstValue("X-Request-ID").get()

    when (val status = response.statusCode()) {
      401 -> {
        val responseData = mapper.readValue(response.body(), GenericErrorResponse::class.java)
        throw UnauthorizedException(responseData.message, requestId)
      }
      404 -> {
        throw NotFoundException(response.request().uri().path, requestId)
      }
      422 -> {
        val unprocessableEntityException = mapper.readValue(response.body(), UnprocessableEntityExceptionResponse::class.java)
        throw UnprocessableEntityException(unprocessableEntityException.message, unprocessableEntityException.errors, requestId)
      }
      else -> {
        val responseData = mapper.readValue(response.body(), GenericErrorResponse::class.java)
        throw GenericServerException(responseData.message, status, requestId)
      }
    }
  }
}
