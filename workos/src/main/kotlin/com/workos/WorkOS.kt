package com.workos

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.common.exceptions.GenericServerException
import com.workos.common.exceptions.NotFoundException
import com.workos.common.exceptions.OauthException
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

class WorkOS(
  val apiKey: String
) {
  init {
    if (apiKey.isNullOrBlank()) {
      throw IllegalArgumentException("Missing API key")
    }
  }

  var apiHostname = "api.workos.com"

  var https: Boolean = true

  var port: Int? = null

  private val version: String = "1.0.0"

  private val httpClient = HttpClient.newBuilder().build()

  private val protocol: String
    get() {
      return if (https) "https" else "http"
    }

  val baseUrl: String
    get() {
      val url = "$protocol://$apiHostname"
      return if (port == null) url else "$url:$port"
    }

  private val requestBuilder =
    HttpRequest.newBuilder()
      .header("Authorization", "Bearer $apiKey")
      .header("User-Agent", "workos-kotlin/$version")
      .header("Content-Type", "application/json")

  private val mapper = jacksonObjectMapper()

  @JvmField
  val directorySync = DirectorySyncApi(this)

  @JvmField
  val organizations = OrganizationsApi(this)

  @JvmField
  val passwordless = PasswordlessApi(this)

  @JvmField
  val portal = PortalApi(this)

  @JvmField
  val sso = SsoApi(this)

  @JvmField
  val webhooks = WebhooksApi()

  init {
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

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

  fun <Res : Any> post(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val requestBuilder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

    return sendRequest(buildRequest(requestBuilder, config), responseType)
  }

  fun <Res : Any> put(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
    val uri = URIBuilder(baseUrl).setPath(path).build()

    val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

    val requestBuilder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

    return sendRequest(buildRequest(requestBuilder, config), responseType)
  }

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

        if (responseData.error != null || responseData.errorDescription != null) {
          throw OauthException(responseData.message, status, requestId, responseData.error, responseData.errorDescription)
        }

        throw GenericServerException(responseData.message, status, requestId)
      }
    }
  }
}
