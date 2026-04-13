// @oagen-ignore-file
package com.workos.common.http

import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min
import kotlin.math.pow

/**
 * Outcome of a single request attempt, consumed by [RetryPolicy.nextDelay].
 */
sealed class AttemptOutcome {
  data class Response(
    val statusCode: Int,
    val retryAfter: String?
  ) : AttemptOutcome()

  data class TransportFailure(
    val error: Throwable
  ) : AttemptOutcome()
}

/**
 * Pure retry-decision logic used by [BaseClient]. Isolated so it can be tested
 * without running real HTTP calls.
 */
class RetryPolicy(
  private val config: RetryConfig = RetryConfig.DEFAULT
) {
  /**
   * Given an attempt number (0-based for the first attempt) and the attempt
   * outcome, return the delay in milliseconds before retrying, or `null` if
   * no more retries should be attempted.
   */
  fun nextDelay(
    attempt: Int,
    outcome: AttemptOutcome,
    maxRetriesOverride: Int? = null
  ): Long? {
    val maxRetries = maxRetriesOverride ?: config.maxRetries
    if (attempt >= maxRetries) return null

    val retryable =
      when (outcome) {
        is AttemptOutcome.Response -> isRetryableStatus(outcome.statusCode)
        is AttemptOutcome.TransportFailure -> true
      }
    if (!retryable) return null

    val retryAfterMs =
      (outcome as? AttemptOutcome.Response)?.retryAfter?.let { parseRetryAfter(it) }
    return retryAfterMs ?: backoffMs(attempt)
  }

  /** Generate an idempotency key for an otherwise unkeyed POST retry. */
  fun generateIdempotencyKey(): String {
    // UUIDs are pretty much unguessable and cheap to produce.
    return "retry-${java.util.UUID.randomUUID()}"
  }

  private fun backoffMs(attempt: Int): Long {
    val exponential = config.baseDelayMs.toDouble() * 2.0.pow(attempt.toDouble())
    val capped = min(exponential, config.maxDelayMs.toDouble())
    val jitterBand = capped * config.jitter
    val jitter = ThreadLocalRandom.current().nextDouble(-jitterBand, jitterBand + 1.0)
    return (capped + jitter).toLong().coerceAtLeast(0L)
  }

  companion object {
    @JvmStatic
    fun isRetryableStatus(status: Int): Boolean = status == 429 || status in 500..599

    /** Parse a `Retry-After` header value (seconds or HTTP-date) into ms. */
    @JvmStatic
    fun parseRetryAfter(value: String): Long? {
      val trimmed = value.trim()
      if (trimmed.isEmpty()) return null
      trimmed.toLongOrNull()?.let {
        if (it < 0) return 0L
        return it * 1000L
      }
      return try {
        val target =
          Instant.from(
            java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
              .parse(trimmed)
          )
        val now = Instant.now()
        val delta = target.toEpochMilli() - now.toEpochMilli()
        if (delta < 0) 0L else delta
      } catch (_: DateTimeParseException) {
        null
      }
    }
  }
}
