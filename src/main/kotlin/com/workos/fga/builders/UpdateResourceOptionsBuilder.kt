package com.workos.fga.builders

import com.workos.fga.types.UpdateResourceOptions

class UpdateResourceOptionsBuilder
  @JvmOverloads
  constructor(
    private var meta: Map<String, String>? = null
  ) {
    fun meta(value: Map<String, String>) = apply { meta = value }

    fun build(): UpdateResourceOptions =
      UpdateResourceOptions(
        meta = this.meta
      )

    companion object {
      @JvmStatic
      fun create(): UpdateResourceOptionsBuilder = UpdateResourceOptionsBuilder()
    }
  }
