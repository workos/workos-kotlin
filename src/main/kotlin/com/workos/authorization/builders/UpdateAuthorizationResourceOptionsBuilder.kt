package com.workos.authorization.builders

import com.workos.authorization.types.UpdateAuthorizationResourceOptions

class UpdateAuthorizationResourceOptionsBuilder @JvmOverloads constructor(
  private var name: String? = null,
  private var description: String? = null
) {
  fun name(value: String) = apply { name = value }

  fun description(value: String) = apply { description = value }

  fun build(): UpdateAuthorizationResourceOptions {
    return UpdateAuthorizationResourceOptions(
      name = this.name,
      description = this.description
    )
  }

  companion object {
    @JvmStatic
    fun create(): UpdateAuthorizationResourceOptionsBuilder {
      return UpdateAuthorizationResourceOptionsBuilder()
    }
  }
}
