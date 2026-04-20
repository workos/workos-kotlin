// @oagen-ignore-file
package com.workos.common.http

/**
 * Description of a single HTTP request built by a service method.
 *
 *  - [method]: upper-case HTTP verb (`GET`, `POST`, ...).
 *  - [path]: URL path starting with `/`, with any path parameters already
 *    substituted in by the caller.
 *  - [queryParams]: serialized query parameters. Lists are passed as repeated
 *    entries; null values are dropped before the wire.
 *  - [body]: request body. Will be serialized as JSON unless it is a
 *    [ByteArray] or [String], in which case it is sent as-is.
 *  - [accessToken]: overrides the default `Authorization` header (used for
 *    per-operation bearer auth like SSO token exchange).
 *  - [requestOptions]: per-request overrides from the caller.
 */
data class RequestConfig
  @JvmOverloads
  constructor(
    /** Upper-case HTTP verb (`GET`, `POST`, etc.). */
    val method: String,
    /** URL path starting with `/`, with path parameters already substituted. */
    val path: String,
    /** Serialized query-string parameters (repeated entries represent lists). */
    val queryParams: List<Pair<String, String>> = emptyList(),
    /** Request body, serialized as JSON unless it is a [String] or [ByteArray]. */
    val body: Any? = null,
    /** Form-encoded body sent as `application/x-www-form-urlencoded`, mutually exclusive with [body]. */
    val formBody: Map<String, String>? = null,
    /** Overrides the default `Authorization` header for per-operation bearer auth. */
    val accessToken: String? = null,
    /** Per-request overrides from the caller. */
    val requestOptions: RequestOptions? = null
  ) {
    init {
      require(path.startsWith("/")) { "RequestConfig.path must start with '/'" }
    }
  }
