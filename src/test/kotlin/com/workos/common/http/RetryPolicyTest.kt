// @oagen-ignore-file
package com.workos.common.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RetryPolicyTest {
  @Test
  fun `retryable status codes`() {
    assertTrue(RetryPolicy.isRetryableStatus(429))
    assertTrue(RetryPolicy.isRetryableStatus(500))
    assertTrue(RetryPolicy.isRetryableStatus(503))
    assertEquals(false, RetryPolicy.isRetryableStatus(400))
    assertEquals(false, RetryPolicy.isRetryableStatus(401))
    assertEquals(false, RetryPolicy.isRetryableStatus(200))
  }

  @Test
  fun `Retry-After seconds parse to milliseconds`() {
    assertEquals(5_000L, RetryPolicy.parseRetryAfter("5"))
    assertEquals(0L, RetryPolicy.parseRetryAfter("0"))
  }

  @Test
  fun `Retry-After negative values clamp to zero`() {
    assertEquals(0L, RetryPolicy.parseRetryAfter("-5"))
  }

  @Test
  fun `Retry-After invalid values return null`() {
    assertNull(RetryPolicy.parseRetryAfter("not-a-number"))
    assertNull(RetryPolicy.parseRetryAfter(""))
  }

  @Test
  fun `Retry-After RFC-1123 dates parse to milliseconds`() {
    val future = DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now().plusSeconds(5).atOffset(ZoneOffset.UTC))
    val parsed = RetryPolicy.parseRetryAfter(future)
    assertNotNull(parsed)
    assertTrue(parsed!! in 1_000L..5_500L)
  }

  @Test
  fun `nextDelay stops once attempt reaches maxRetries`() {
    val policy = RetryPolicy(RetryConfig(maxRetries = 2, baseDelayMs = 10, maxDelayMs = 100, jitter = 0.0))
    assertNotNull(policy.nextDelay(0, AttemptOutcome.Response(500, null)))
    assertNotNull(policy.nextDelay(1, AttemptOutcome.Response(500, null)))
    assertNull(policy.nextDelay(2, AttemptOutcome.Response(500, null)))
  }

  @Test
  fun `nextDelay refuses to retry non-retryable status`() {
    val policy = RetryPolicy(RetryConfig(maxRetries = 3))
    assertNull(policy.nextDelay(0, AttemptOutcome.Response(401, null)))
    assertNull(policy.nextDelay(0, AttemptOutcome.Response(400, null)))
  }

  @Test
  fun `nextDelay retries transport failures`() {
    val policy = RetryPolicy(RetryConfig(maxRetries = 2, baseDelayMs = 5, maxDelayMs = 20, jitter = 0.0))
    val d0 = policy.nextDelay(0, AttemptOutcome.TransportFailure(RuntimeException("boom")))
    assertNotNull(d0)
  }

  @Test
  fun `nextDelay honors per-request maxRetries override`() {
    val policy = RetryPolicy(RetryConfig(maxRetries = 5))
    assertNull(policy.nextDelay(0, AttemptOutcome.Response(500, null), maxRetriesOverride = 0))
    assertNotNull(policy.nextDelay(0, AttemptOutcome.Response(500, null), maxRetriesOverride = 1))
  }

  @Test
  fun `nextDelay prefers Retry-After over backoff`() {
    val policy = RetryPolicy(RetryConfig(maxRetries = 3, baseDelayMs = 10, maxDelayMs = 20, jitter = 0.0))
    val delay = policy.nextDelay(0, AttemptOutcome.Response(429, "7"))
    assertEquals(7_000L, delay)
  }

  @Test
  fun `backoff is bounded by maxDelayMs`() {
    val policy = RetryPolicy(RetryConfig(maxRetries = 10, baseDelayMs = 10, maxDelayMs = 15, jitter = 0.0))
    // Without jitter, the backoff for attempt 10 should saturate to maxDelayMs.
    val delay = policy.nextDelay(9, AttemptOutcome.Response(500, null))
    assertNotNull(delay)
    assertEquals(true, delay!! <= 15L)
  }

  @Test
  fun `generateIdempotencyKey is deterministic for the same seed and distinct for different seeds`() {
    val policy = RetryPolicy()
    val a = policy.generateIdempotencyKey("POST|/things|body-a")
    val b = policy.generateIdempotencyKey("POST|/things|body-a")
    val c = policy.generateIdempotencyKey("POST|/things|body-b")
    assertEquals(true, a.isNotBlank())
    assertEquals(true, b.isNotBlank())
    assertEquals(a, b)
    assertTrue(a != c, "different seeds should produce different keys")
  }
}
