// @oagen-ignore-file
package com.workos.common.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EncodePathSegmentTest {
  @Test
  fun `passes through plain alphanumerics`() {
    assertEquals("user_01HXYZ", encodePathSegment("user_01HXYZ"))
  }

  @Test
  fun `encodes path-reserved characters`() {
    assertEquals("foo%2Fbar", encodePathSegment("foo/bar"))
    assertEquals("a%3Fb", encodePathSegment("a?b"))
    assertEquals("a%23b", encodePathSegment("a#b"))
  }

  @Test
  fun `encodes spaces as percent-20 not plus`() {
    // URLEncoder yields `+` for spaces (form-encoding); for path segments we
    // need `%20` so that the server doesn't treat the value as a literal `+`.
    assertEquals("hello%20world", encodePathSegment("hello world"))
  }

  @Test
  fun `encodes non-ASCII as UTF-8 percent escapes`() {
    assertEquals("caf%C3%A9", encodePathSegment("café"))
  }

  @Test
  fun `does not percent-encode tilde`() {
    // RFC 3986 marks `~` as unreserved; URLEncoder's form encoding wrongly
    // emits `%7E`, and we must decode it back so the segment is canonical.
    assertEquals("a~b", encodePathSegment("a~b"))
    assertEquals("~", encodePathSegment("~"))
  }

  @Test
  fun `prevents path traversal by encoding slashes`() {
    // A literal `..` is not by itself dangerous (the server resolves it as a
    // segment), but any embedded `/` must be encoded so a malicious id
    // cannot escape its own segment.
    assertEquals("..", encodePathSegment(".."))
    assertEquals("..%2Fadmin", encodePathSegment("../admin"))
    assertEquals("foo%2F..%2Fbar", encodePathSegment("foo/../bar"))
  }

  @Test
  fun `encodes wider unicode (multi-byte) correctly`() {
    // Emoji forces a 4-byte UTF-8 sequence to exercise the percent loop.
    assertEquals("%F0%9F%94%92", encodePathSegment("🔒"))
  }

  @Test
  fun `encodes plus sign so it round-trips as plus, not space`() {
    // URLEncoder leaves `+` alone; without post-processing the server would
    // decode it back to a space.
    assertEquals("a%2Bb", encodePathSegment("a+b"))
  }

  @Test
  fun `encodes asterisk for safety`() {
    assertEquals("a%2Ab", encodePathSegment("a*b"))
  }
}
