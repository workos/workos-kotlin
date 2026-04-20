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
    val method: String,
    val path: String,
    val queryParams: List<Pair<String, String>> = emptyList(),
    val body: Any? = null,
    val formBody: Map<String, String>? = null,
    val accessToken: String? = null,
    val requestOptions: RequestOptions? = null
  ) {
    init {
      require(path.startsWith("/")) { "RequestConfig.path must start with '/'" }
    }
  }
