// @oagen-ignore-file
package com.workos.common.http

/**
 * Per-request overrides applied on top of the client-wide configuration.
 *
 * All fields are optional; anything left as `null` falls back to the client
 * defaults. Use [Builder] (Java) or the Kotlin named-argument constructor.
 */
class RequestOptions
  @JvmOverloads
  constructor(
    /** Extra headers merged into the request (e.g. tracing or feature-flag headers). */
    @JvmField val additionalHeaders: Map<String, String> = emptyMap(),
    /** Per-request timeout in milliseconds, overriding the client default. */
    @JvmField val timeoutMillis: Long? = null,
    /** Maximum number of retries for this request, overriding [RetryConfig.maxRetries]. */
    @JvmField val maxRetries: Int? = null,
    /** Base URL override for this request (e.g. for staging environments). */
    @JvmField val baseUrl: String? = null,
    /** Idempotency key sent via the `Idempotency-Key` header for safe retries. */
    @JvmField val idempotencyKey: String? = null,
    /** API key override for this request, taking precedence over the client-wide key. */
    @JvmField val apiKey: String? = null
  ) {
    /** Fluent builder for Java callers who cannot use Kotlin named arguments. */
    class Builder {
      private val additionalHeaders: MutableMap<String, String> = LinkedHashMap()
      private var timeoutMillis: Long? = null
      private var maxRetries: Int? = null
      private var baseUrl: String? = null
      private var idempotencyKey: String? = null
      private var apiKey: String? = null

      /** Add a single header to the request. */
      fun header(
        name: String,
        value: String
      ) = apply { additionalHeaders[name] = value }

      /** Merge multiple headers into the request. */
      fun additionalHeaders(headers: Map<String, String>) = apply { additionalHeaders.putAll(headers) }

      /** Set the per-request timeout in milliseconds. */
      fun timeoutMillis(millis: Long) = apply { timeoutMillis = millis }

      /** Set the maximum number of retries for this request. */
      fun maxRetries(count: Int) = apply { maxRetries = count }

      /** Override the base URL for this request. */
      fun baseUrl(url: String) = apply { baseUrl = url }

      /** Set the idempotency key for safe retries. */
      fun idempotencyKey(key: String) = apply { idempotencyKey = key }

      /** Override the API key for this request. */
      fun apiKey(key: String) = apply { apiKey = key }

      /** Build the [RequestOptions] instance. */
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

    /** Factory for obtaining a [Builder] instance. */
    companion object {
      /** Create a new [Builder]. */
      @JvmStatic fun builder(): Builder = Builder()
    }
  }
