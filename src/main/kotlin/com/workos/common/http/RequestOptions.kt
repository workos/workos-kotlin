// @oagen-ignore-file
package com.workos.common.http

/**
 * Per-request overrides applied on top of the client-wide configuration.
 *
 * All fields are optional; anything left as `null` falls back to the client
 * defaults. Use [Builder] (Java) or the Kotlin named-argument constructor.
 */
class RequestOptions private constructor(
  @JvmField val additionalHeaders: Map<String, String>,
  @JvmField val timeoutMillis: Long?,
  @JvmField val maxRetries: Int?,
  @JvmField val baseUrl: String?,
  @JvmField val idempotencyKey: String?,
  @JvmField val apiKey: String?
) {
  class Builder {
    private var additionalHeaders: MutableMap<String, String> = LinkedHashMap()
    private var timeoutMillis: Long? = null
    private var maxRetries: Int? = null
    private var baseUrl: String? = null
    private var idempotencyKey: String? = null
    private var apiKey: String? = null

    fun header(
      name: String,
      value: String
    ) = apply { additionalHeaders[name] = value }

    fun additionalHeaders(headers: Map<String, String>) = apply { additionalHeaders.putAll(headers) }

    fun timeoutMillis(millis: Long) = apply { timeoutMillis = millis }

    fun maxRetries(count: Int) = apply { maxRetries = count }

    fun baseUrl(url: String) = apply { baseUrl = url }

    fun idempotencyKey(key: String) = apply { idempotencyKey = key }

    fun apiKey(key: String) = apply { apiKey = key }

    fun build(): RequestOptions =
      RequestOptions(
        additionalHeaders = additionalHeaders.toMap(),
        timeoutMillis = timeoutMillis,
        maxRetries = maxRetries,
        baseUrl = baseUrl,
        idempotencyKey = idempotencyKey,
        apiKey = apiKey
      )
  }

  companion object {
    @JvmStatic fun builder(): Builder = Builder()
  }
}
