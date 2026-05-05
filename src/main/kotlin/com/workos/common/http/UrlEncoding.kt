package com.workos.common.http

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Percent-encodes a single URL path segment per RFC 3986.
 *
 * `java.net.URLEncoder` targets `application/x-www-form-urlencoded`, which
 * differs from path-segment encoding in three ways: spaces become `+`, `*` is
 * left unencoded, and `~` is encoded. This adjusts those so the result is safe
 * to interpolate into a path component such that path traversal sequences
 * (`/`, `..`) and reserved characters (`?`, `#`) cannot escape the segment.
 */
fun encodePathSegment(value: String): String =
  URLEncoder
    .encode(value, StandardCharsets.UTF_8)
    .replace("+", "%20")
    .replace("*", "%2A")
    .replace("%7E", "~")
