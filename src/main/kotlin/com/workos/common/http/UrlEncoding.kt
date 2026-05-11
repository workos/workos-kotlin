// @oagen-ignore-file
// RFC 3986 path-segment percent-encoding helper used by every generated
// service to encode interpolated path parameters (e.g. `${encodePathSegment(id)}`).
//
// `URLEncoder.encode` performs `application/x-www-form-urlencoded` encoding,
// which is wrong for path segments in three ways that matter here:
//   - it encodes space as `+` (path segments require `%20`)
//   - it does not encode `+` (which has no special meaning in a path but
//     is decoded back to space on the other side, mangling the value)
//   - it percent-encodes `~` (which is RFC 3986 unreserved and must NOT be
//     encoded), and likewise does not consistently leave `*` alone.
//
// We start from the form encoder's output and post-process to align with
// RFC 3986 Section 3.3 path-segment rules. Crucially, `/` is encoded as
// `%2F` so a malicious id like `..%2Fadmin` cannot escape its segment.
package com.workos.common.http

import java.net.URLEncoder

/**
 * Percent-encode [value] for safe interpolation as a single URI path segment
 * per RFC 3986 §3.3. All reserved characters (`/`, `?`, `#`, etc.) and
 * sub-delims that could change parsing are encoded; `~` and other unreserved
 * characters are left alone.
 */
fun encodePathSegment(value: String): String {
  val formEncoded = URLEncoder.encode(value, Charsets.UTF_8)
  val out = StringBuilder(formEncoded.length)
  var i = 0
  while (i < formEncoded.length) {
    val c = formEncoded[i]
    when {
      // form encoder emits '+' for space; path segments use %20
      c == '+' -> {
        out.append("%20")
        i += 1
      }
      // form encoder leaves '*' unescaped; it's a sub-delim, encode it for safety
      c == '*' -> {
        out.append("%2A")
        i += 1
      }
      c == '%' && i + 2 < formEncoded.length -> {
        val hex = formEncoded.substring(i + 1, i + 3).uppercase()
        when (hex) {
          // '~' is unreserved — must not be percent-encoded
          "7E" -> out.append('~')
          else -> {
            out.append('%')
            out.append(hex)
          }
        }
        i += 3
      }
      else -> {
        out.append(c)
        i += 1
      }
    }
  }
  return out.toString()
}
