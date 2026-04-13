// @oagen-ignore-file
package com.workos.common.http

/**
 * Client-wide retry behaviour. Per-request overrides live on [RequestOptions].
 */
data class RetryConfig(
  @JvmField val maxRetries: Int = 3,
  @JvmField val baseDelayMs: Long = 500,
  @JvmField val maxDelayMs: Long = 8_000,
  @JvmField val jitter: Double = 0.25
) {
  companion object {
    @JvmField val DEFAULT: RetryConfig = RetryConfig()

    /** A retry config that performs no retries — useful for tests. */
    @JvmField val DISABLED: RetryConfig = RetryConfig(maxRetries = 0)
  }
}
