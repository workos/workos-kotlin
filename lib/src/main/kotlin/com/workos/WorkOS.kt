package com.workos

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

    fun get(path: String): HttpResponse<String> {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val request = requestBuilder.copy().GET().uri(uri).build()

        return httpClient.send(request, BodyHandlers.ofString())
    }
}
