package com.workos

import org.apache.http.client.utils.URIBuilder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

const val DEFAULT_HOSTNAME = "api.workos.com"

class WorkOS(
    val apiKey: String,
    val https: Boolean = true,
    val apiHostname: String = DEFAULT_HOSTNAME,
    val port: Int? = null
) {
    val protocol: String = if (https) "https" else "http"

    val client = HttpClient.newBuilder().build()

    val version: String = "1.0.0"

    val baseURL: String
        get() {
            val url = "$protocol://$apiHostname"
            return if (port == null) url else "$url:$port"
        }

    val requestBuilder =
        HttpRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("User-Agent", "workos-kotlin/$version")

    fun get(path: String): HttpResponse<String> {
        val uri = URIBuilder(baseURL).setPath(path).build()

        val request = requestBuilder.copy().GET().uri(uri).build()

        return client.send(request, BodyHandlers.ofString())
    }
}
