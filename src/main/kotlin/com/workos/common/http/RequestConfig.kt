package com.workos.common.http

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * Configuration for HTTP Requests.
 *
 * @param params Query parameters appended to the URL.
 * @param headers Headers of the request.
 * @param data The body of the request.
 */
class RequestConfig(
  val params: Map<String, String>? = null,
  val headers: Map<String, String>? = null,
  val data: Any? = null
) {
  /** @suppress */
  companion object {
    @JvmStatic
    fun builder(): RequestConfigBuilder {
      return RequestConfigBuilder()
    }

    /** Helper method for converting data into params. */
    infix fun <T : Any> toMap(obj: T): Map<String, Any?> {
      val params =
        (obj::class as KClass<T>).memberProperties.associate { prop: KProperty1<T, *> ->
          prop.name.toSnakeCase() to
            prop.get(obj)?.let { value ->
              if (value::class.isData) {
                toMap(value)
              } else {
                value.toString()
              }
            }
        }

      return params.filterValues { it != null }
    }

    /** Convert camel case to snake case. */
    fun String.toSnakeCase() = replace(humps, "_").lowercase()

    private val humps = "(?<=.)(?=\\p{Upper})".toRegex()
  }

  /** Builder class for creating [RequestConfig]. */
  class RequestConfigBuilder {
    private var params: Map<String, String> = emptyMap()

    private var headers: Map<String, String> = emptyMap()

    private var data: Any? = null

    /** Set the request parameters. */
    fun params(value: Map<String, String>) = apply { params = value }

    /** Set the request headers. */
    fun headers(value: Map<String, String?>) = apply { headers = value as Map<String, String> }

    /** Set the request body. */
    fun data(value: Any) = apply { data = value }

    /** Creates an instance of [RequestConfig] with the given params. */
    fun build(): RequestConfig {
      return RequestConfig(params, headers, data)
    }
  }
}
