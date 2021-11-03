package com.workos.common.http

class RequestConfig(
  val params: Map<String, String>? = null,
  val headers: Map<String, String>? = null,
  val data: Any? = null,
) {
  companion object {
    @JvmStatic
    fun builder(): Builder {
      return Builder()
    }
  }

  class Builder {
    var params: Map<String, String> = emptyMap()

    var headers: Map<String, String> = emptyMap()

    var data: Any? = null

    fun params(value: Map<String, String>) = apply { params = value }

    fun headers(value: Map<String, String>) = apply { headers = value }

    fun data(value: Any) = apply { data = value }

    fun build(): RequestConfig {
      return RequestConfig(params, headers, data)
    }
  }
}
