package com.workos

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.common.exceptions.GenericServerException
import com.workos.common.exceptions.NotFoundException
import com.workos.common.exceptions.OauthException
import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.UnprocessableEntityException
import com.workos.common.options.RequestOptions
import com.workos.common.responses.GenericErrorResponse
import com.workos.common.responses.UnprocessableEntityExceptionResponse
import org.apache.http.client.utils.URIBuilder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import kotlin.collections.Map

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
            return if (https == true) "https" else "http"
        }

    private val baseURL: String
        get() {
            val url = "$protocol://$apiHostname"
            return if (port == null) url else "$url:$port"
        }

    private val requestBuilder =
        HttpRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("User-Agent", "workos-kotlin/$version")

    private val mapper = jacksonObjectMapper()

    init {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    fun <Res : Any> get(path: String, responseType: Class<Res>, params: Map<String, String> = mapOf()): Res {
        val uri = URIBuilder(baseURL).setPath(path)

        for (item in params.entries) {
            uri.addParameter(item.key, item.value)
        }

        val request = requestBuilder.copy().GET().uri(uri.build()).build()

        return sendRequest(request, responseType)
    }

    fun <Res : Any> post(path: String, entity: Any, responseType: Class<Res>, options: RequestOptions? = null): Res {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val body = mapper.writeValueAsString(entity)

        val builder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

        if (options?.idempotencyKey !== null) {
            builder.header("Idempotency-Key", options.idempotencyKey)
        }

        return sendRequest(builder.build(), responseType)
    }

    fun <Res : Any> put(path: String, entity: Any, responseType: Class<Res>, options: RequestOptions? = null): Res {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val body = mapper.writeValueAsString(entity)

        val builder = requestBuilder.copy().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri)

        if (options?.idempotencyKey !== null) {
            builder.header("Idempotency-Key", options.idempotencyKey)
        }

        return sendRequest(builder.build(), responseType)
    }

    fun <Res : Any> delete(path: String, responseType: Class<Res>): Res {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val request = requestBuilder.copy().DELETE().uri(uri).build()

        return sendRequest(request, responseType)
    }

    private fun <Res : Any> sendRequest(request: HttpRequest, responseType: Class<Res>): Res {
        val response = httpClient.send(request, BodyHandlers.ofString())

        if (response.statusCode() >= 400) {
            handleResponseError(response)
        }

        return mapper.readValue(response.body(), responseType)
    }

    private fun handleResponseError(response: HttpResponse<String>) {
        val requestId = response.headers().firstValue("X-Request-ID").get()
        val status = response.statusCode()

        when (status) {
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
