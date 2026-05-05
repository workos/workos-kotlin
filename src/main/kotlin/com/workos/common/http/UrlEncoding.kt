package com.workos.common.http

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Percent-encodes a single URL path segment so that user-supplied values are
 * safe to interpolate into a path component.
 *
 * Built on `java.net.URLEncoder`, which targets
 * `application/x-www-form-urlencoded`. That encoding differs from path-segment
 * encoding in a few ways, which this helper corrects:
 *
 *  - spaces become `+` instead of `%20`
 *  - `*` is left unencoded
 *  - `~` is encoded even though it is unreserved in RFC 3986
 *  - `.` is left unencoded, so a value of `..` would pass through verbatim
 *    and could be interpreted as a path-traversal segment by an HTTP client
 *    that normalises dot segments
 *
 * The result is safe to interpolate into a path component: segment-boundary
 * characters (`/`), query/fragment delimiters (`?`, `#`), and dot segments
 * (`.`, `..`) are all percent-encoded and therefore cannot escape the
 * segment.
 */
fun encodePathSegment(value: String): String =
  URLEncoder
    .encode(value, StandardCharsets.UTF_8)
    .replace("+", "%20")
    .replace("*", "%2A")
    .replace(".", "%2E")
    .replace("%7E", "~")
