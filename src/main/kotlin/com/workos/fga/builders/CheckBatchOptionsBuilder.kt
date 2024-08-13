package com.workos.fga.builders

import com.workos.fga.types.CheckBatchOptions
import com.workos.fga.types.WarrantCheckOptions

class CheckBatchOptionsBuilder @JvmOverloads constructor(
  private var checks: List<WarrantCheckOptions>,
  private var debug: Boolean? = null,
) {
  fun checks(value: List<WarrantCheckOptions>) = apply { checks = value }

  fun debug(value: Boolean) = apply { debug = value }

  fun build(): CheckBatchOptions {
    return CheckBatchOptions(
      checks = this.checks,
      debug = this.debug,
    )
  }

  companion object {
    @JvmStatic
    fun create(checks: List<WarrantCheckOptions>, debug: Boolean? = null): CheckBatchOptionsBuilder {
      return CheckBatchOptionsBuilder(checks, debug)
    }
  }
}
