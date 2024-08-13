package com.workos.fga.builders

import com.workos.fga.types.CreateResourceOptions

class CreateResourceOptionsBuilder @JvmOverloads constructor(
  private var resourceType: String,
  private var resourceId: String? = null,
  private var meta: Map<String, String>? = null,
) {
  fun resourceType(value: String) = apply { resourceType = value }

  fun resourceId(value: String) = apply { resourceId = value }

  fun meta(value: Map<String, String>) = apply { meta = value }

  fun build(): CreateResourceOptions {
    return CreateResourceOptions(
      resourceType = this.resourceType,
      resourceId = this.resourceId,
      meta = this.meta,
    )
  }

  companion object {
    @JvmStatic
    fun create(resourceType: String): CreateResourceOptionsBuilder {
      return CreateResourceOptionsBuilder(resourceType)
    }
  }
}
