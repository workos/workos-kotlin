// @oagen-ignore-file
package com.workos.common.http

/**
 * Client-wide retry behaviour. Per-request overrides live on [RequestOptions].
 */
data class RetryConfig(
  /** Maximum number of retry attempts after the initial request. */
  @JvmField val maxRetries: Int = 3,
  /** Initial delay in milliseconds before the first retry (doubled on each subsequent attempt). */
  @JvmField val baseDelayMs: Long = 500,
  /** Upper bound in milliseconds for the exponential backoff delay. */
  @JvmField val maxDelayMs: Long = 8_000,
  /** Fractional jitter band applied to each delay (e.g. `0.25` means +/-25%). */
  @JvmField val jitter: Double = 0.25
) {
  /** Predefined [RetryConfig] instances. */
  companion object {
    /** Default retry configuration: 3 retries, 500 ms base delay, 8 s cap, 25% jitter. */
    @JvmField val DEFAULT: RetryConfig = RetryConfig()

    /** A retry config that performs no retries -- useful for tests. */
    @JvmField val DISABLED: RetryConfig = RetryConfig(maxRetries = 0)
  }
}
