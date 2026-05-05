package com.workos.test.common.http

import com.workos.common.http.encodePathSegment
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlEncodingTest {
  @Test
  fun encodesForwardSlashSoSegmentBoundariesCannotBeForged() {
    assertEquals("foo%2Fbar", encodePathSegment("foo/bar"))
  }

  @Test
  fun encodesDotSegmentsThatWouldOtherwiseTraverseUpward() {
    assertEquals("%2E%2E", encodePathSegment(".."))
    assertEquals("a%2F%2E%2E%2Fb", encodePathSegment("a/../b"))
  }

  @Test
  fun encodesSpacesAsPercent20RatherThanPlus() {
    assertEquals("hello%20world", encodePathSegment("hello world"))
  }

  @Test
  fun encodesQueryAndFragmentDelimiters() {
    assertEquals("a%3Fb%23c", encodePathSegment("a?b#c"))
  }

  @Test
  fun encodesPlusLiteral() {
    assertEquals("a%2Bb", encodePathSegment("a+b"))
  }

  @Test
  fun encodesAsteriskWhichUrlEncoderLeavesAlone() {
    assertEquals("%2A", encodePathSegment("*"))
  }

  @Test
  fun encodesCarriageReturnAndLineFeed() {
    assertEquals("a%0D%0Ab", encodePathSegment("a\r\nb"))
  }

  @Test
  fun leavesRfc3986UnreservedCharactersUntouchedExceptDot() {
    assertEquals("AZaz09-_~", encodePathSegment("AZaz09-_~"))
  }

  @Test
  fun returnsEmptyStringForEmptyInput() {
    assertEquals("", encodePathSegment(""))
  }
}
