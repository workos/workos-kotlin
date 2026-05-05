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
}
