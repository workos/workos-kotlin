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
import com.workos.sso.SSOApi
import org.apache.http.client.utils.URIBuilder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

class WorkOS(
    val apiKey: String
) {
    var apiHostname = "api.workos.com"

    var https: Boolean = true

    var port: Int? = null

    private val version: String = "1.0.0"

    private val httpClient = HttpClient.newBuilder().build()

    private val protocol: String
        get() {
            return if (https) "https" else "http"
        }

    val baseURL: String
        get() {
            val url = "$protocol://$apiHostname"
            return if (port == null) url else "$url:$port"
        }

    private val requestBuilder =
        HttpRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("User-Agent", "workos-kotlin/$version")

    private val mapper = jacksonObjectMapper()

    val directorySync by lazy {
        DirectorySyncApi(this)
    }

    val sso by lazy {
        SSOApi(this)
    }

    init {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    fun <Res : Any> get(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
        val uri = URIBuilder(baseURL).setPath(path)

        if (config != null) {
            for (param in config.params) {
                uri.addParameter(param.key, param.value)
            }
        }

        val requestBuilder = requestBuilder.copy().GET().uri(uri.build())

        return sendRequest(buildRequest(requestBuilder, config), responseType)
    }

    fun <Res : Any> post(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

        val requestBuilder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

        return sendRequest(buildRequest(requestBuilder, config), responseType)
    }

    fun <Res : Any> put(path: String, responseType: Class<Res>, config: RequestConfig? = null): Res {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val body = if (config?.data != null) mapper.writeValueAsString(config.data) else ""

        val requestBuilder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

        return sendRequest(buildRequest(requestBuilder, config), responseType)
    }

    fun delete(path: String, config: RequestConfig? = null): HttpResponse<String> {
        val uri = URIBuilder(baseURL).setPath(path).build()
        val requestBuilder = requestBuilder.copy().DELETE().uri(uri)
        return sendRequest(buildRequest(requestBuilder, config))
    }

    private fun buildRequest(requestBuilder: HttpRequest.Builder, config: RequestConfig? = null): HttpRequest {
        if (config != null) {
            for (header in config.headers) {
                requestBuilder.setHeader(header.key, header.value)
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
                throw UnauthorizedException(requestId)
            }
            404 -> {
                throw NotFoundException(response.request().uri().path, requestId)
            }
            422 -> {
                val errors = mapper.readValue(response.body(), UnprocessableEntityExceptionResponse::class.java).errors
                throw UnprocessableEntityException(errors, requestId)
            }
            else -> {
                val responseData = mapper.readValue(response.body(), GenericErrorResponse::class.java)

                if (responseData.error != null || responseData.error_description != null) {
                    throw OauthException(status, requestId, responseData.error, responseData.error_description)
                }

                throw GenericServerException(status, responseData.message, requestId)
            }
        }
    }
}
