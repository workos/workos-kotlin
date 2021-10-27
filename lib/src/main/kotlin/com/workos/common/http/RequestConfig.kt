package com.workos.common.http

class RequestConfig(
    val params: Map<String, String>,

    val headers: Map<String, String>,

    val data: Any?,
) {
    companion object {
        fun builder(): RequestConfigBuilder {
            return RequestConfigBuilder()
        }
    }

    class RequestConfigBuilder {
        var params: Map<String, String> = mapOf()

        var headers: Map<String, String> = mapOf()

        var data: Any? = null

        fun params(value: Map<String, String>) = apply { params = value }

        fun headers(value: Map<String, String>) = apply { headers = value }

        fun data(value: Any) = apply { data = value }

        fun build(): RequestConfig {
            return RequestConfig(params, headers, data)
        }
    }
}
