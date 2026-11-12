// @oagen-ignore-file
@file:JvmName("GitHubActions")

package com.workos.workloadidentity.githubactions

import com.workos.workloadidentity.IdentityTokenProvider
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

private const val REQUEST_URL_VARIABLE = "ACTIONS_ID_TOKEN_REQUEST_URL"

private const val REQUEST_TOKEN_VARIABLE = "ACTIONS_ID_TOKEN_REQUEST_TOKEN"

private const val MISSING_ENDPOINT =
  "GitHub Actions did not provide an OIDC token endpoint. " +
    "Add `permissions: id-token: write` to the workflow or job."

private val TIMEOUT: Duration = Duration.ofSeconds(10)

private const val VALUE_KEY = "\"value\""

private const val UNICODE_ESCAPE_LENGTH = 4

/**
 * An [IdentityTokenProvider] for GitHub Actions. It asks the workflow run's
 * OIDC endpoint for a token issued for [audience].
 *
 * The workflow or job needs `permissions: id-token: write`. The conventional
 * audience for WorkOS is `https://api.workos.com`.
 *
 * Kotlin usage:
 * ```kotlin
 * val provider = githubActions("https://api.workos.com")
 * ```
 *
 * Java usage:
 * ```java
 * IdentityTokenProvider provider = GitHubActions.githubActions("https://api.workos.com");
 * ```
 *
 * The provider makes one request per call, with a 10-second timeout and no
 * redirects. It does not cache and does not retry; the SDK calls it again
 * when an exchange attempt fails.
 */
fun githubActions(audience: String): IdentityTokenProvider = githubActions(audience) { System.getenv(it) }

/** [githubActions] with the environment lookup supplied by the caller, for tests. Hidden from Java. */
@JvmSynthetic
internal fun githubActions(
  audience: String,
  env: (String) -> String?
): IdentityTokenProvider {
  require(audience.isNotBlank()) { "audience is required" }
  val http =
    HttpClient
      .newBuilder()
      .followRedirects(HttpClient.Redirect.NEVER)
      .connectTimeout(TIMEOUT)
      .build()
  return IdentityTokenProvider {
    // Both variables are read on every call: a runner sets them per job.
    val url = env(REQUEST_URL_VARIABLE)?.takeIf { it.isNotEmpty() } ?: error(MISSING_ENDPOINT)
    val requestToken = env(REQUEST_TOKEN_VARIABLE)?.takeIf { it.isNotEmpty() } ?: error(MISSING_ENDPOINT)

    // The URL GitHub provides already has a query string; add to it.
    val separator = if (url.contains('?')) '&' else '?'
    val request =
      HttpRequest
        .newBuilder(URI.create("$url${separator}audience=${percentEncode(audience)}"))
        .timeout(TIMEOUT)
        .header("Authorization", "Bearer $requestToken")
        .header("Accept", "application/json")
        .GET()
        .build()
    val response = http.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) {
      throw IOException("The GitHub Actions OIDC token endpoint returned HTTP ${response.statusCode()}.")
    }
    tokenFrom(response.body())?.takeIf { it.isNotEmpty() }
      ?: throw IOException("The GitHub Actions OIDC token endpoint returned no token.")
  }
}

/** Percent-encode a query value. `URLEncoder` writes a space as `+`, which is form encoding; a URL wants `%20`. */
private fun percentEncode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8).replace("+", "%20")

/**
 * The string value of the response's `value` field, or `null` when there is
 * none. The response is a small flat JSON object, and this module takes no
 * JSON library, so the one field is read directly.
 *
 * The read is a plain scan. A regular expression is not used on purpose:
 * `java.util.regex` recurses once per character for this shape, and a real
 * token is long enough to overflow a thread's stack.
 */
@JvmSynthetic
internal fun tokenFrom(json: String): String? {
  var key = json.indexOf(VALUE_KEY)
  while (key >= 0) {
    var cursor = skipWhitespace(json, key + VALUE_KEY.length)
    // `"value"` followed by anything but a colon is a string that happens to say "value", not the key.
    if (cursor < json.length && json[cursor] == ':') {
      cursor = skipWhitespace(json, cursor + 1)
      return if (cursor < json.length && json[cursor] == '"') readJsonString(json, cursor + 1) else null
    }
    key = json.indexOf(VALUE_KEY, key + 1)
  }
  return null
}

private fun skipWhitespace(
  json: String,
  from: Int
): Int {
  var cursor = from
  while (cursor < json.length && json[cursor].isWhitespace()) cursor += 1
  return cursor
}

/** Read a JSON string whose opening quote is just before [from]. Returns `null` when it is not closed or an escape is cut short. */
private fun readJsonString(
  json: String,
  from: Int
): String? {
  val result = StringBuilder()
  var cursor = from
  while (cursor < json.length) {
    val char = json[cursor]
    when {
      char == '"' -> return result.toString()
      char != '\\' -> result.append(char)
      else -> {
        cursor += 1
        if (cursor >= json.length) return null
        when (val escaped = json[cursor]) {
          'u' -> {
            val digits = json.substring(cursor + 1, minOf(cursor + 1 + UNICODE_ESCAPE_LENGTH, json.length))
            val code = digits.takeIf { it.length == UNICODE_ESCAPE_LENGTH }?.toIntOrNull(16) ?: return null
            result.append(code.toChar())
            cursor += UNICODE_ESCAPE_LENGTH
          }
          'n' -> result.append('\n')
          'r' -> result.append('\r')
          't' -> result.append('\t')
          'b' -> result.append('\b')
          'f' -> result.append('\u000C')
          else -> result.append(escaped)
        }
      }
    }
    cursor += 1
  }
  return null
}
