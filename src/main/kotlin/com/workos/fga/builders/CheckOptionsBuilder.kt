package com.workos.fga.builders

import com.workos.fga.types.CheckOptions
import com.workos.fga.types.WarrantCheckOptions

class CheckOptionsBuilder @JvmOverloads constructor(
  private var op: String? = null,
  private var checks: List<WarrantCheckOptions>,
  private var debug: Boolean? = null,
) {
  constructor(checks: List<WarrantCheckOptions>, debug: Boolean? = null) : this(null, checks, debug)

  fun op(value: String) = apply { op = value }

  fun checks(value: List<WarrantCheckOptions>) = apply { checks = value }

  fun debug(value: Boolean) = apply { debug = value }

  fun build(): CheckOptions {
    return CheckOptions(
      op = this.op,
      checks = this.checks,
      debug = this.debug,
    )
  }

  companion object {
    @JvmStatic
    fun create(op: String, checks: List<WarrantCheckOptions>, debug: Boolean? = null): CheckOptionsBuilder {
      return CheckOptionsBuilder(op, checks, debug)
    }

    @JvmStatic
    fun create(checks: List<WarrantCheckOptions>, debug: Boolean? = null): CheckOptionsBuilder {
      return CheckOptionsBuilder("", checks, debug)
    }
  }
}
