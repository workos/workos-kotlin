// @oagen-ignore-file
// Hand-maintained URL/encoding helpers shared by hand-written authorization
// URL builders (e.g. SSO, AuthKit). These intentionally live in the common
// package so the various OAuth-style URL builders don't duplicate the same
// "join base + path + form-encoded query" logic.
package com.workos.common.http

import com.workos.WorkOS
import java.net.URLEncoder
import java.security.SecureRandom
import java.util.Base64

/**
 * Build a fully-qualified WorkOS URL from a path and a list of query
 * parameter pairs. The pair list (rather than a `Map`) preserves duplicate
 * keys, which OAuth URLs use for repeated `provider_scopes` values.
 *
 * Returns `"$apiBaseUrl$path"` when [params] is empty; otherwise appends
 * `?k=v&k=v` with each key and value form-URL-encoded.
 */
internal fun buildAuthUrl(
  workos: WorkOS,
  path: String,
  params: List<Pair<String, String>>
): String {
  val base = workos.apiBaseUrl.trimEnd('/')
  if (params.isEmpty()) return "$base$path"
  val query = params.joinToString("&") { (k, v) -> "${urlEncodeQuery(k)}=${urlEncodeQuery(v)}" }
  return "$base$path?$query"
}

/** Form-URL-encode a query parameter name or value. */
internal fun urlEncodeQuery(value: String): String = URLEncoder.encode(value, Charsets.UTF_8)

/**
 * Generate a cryptographically random base64url state value (no padding) for
 * OAuth/CSRF protection. 32 bytes of entropy produces a 43-character token.
 */
internal fun randomOAuthState(): String {
  val bytes = ByteArray(32)
  SecureRandom().nextBytes(bytes)
  return Base64
    .getUrlEncoder()
    .withoutPadding()
    .encodeToString(bytes)
}
