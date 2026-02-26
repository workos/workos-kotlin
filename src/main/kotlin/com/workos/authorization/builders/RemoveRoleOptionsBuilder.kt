package com.workos.authorization.builders

import com.workos.authorization.types.RemoveRoleOptions

class RemoveRoleOptionsBuilder @JvmOverloads constructor(
  private var roleSlug: String,
  private var resourceId: String? = null,
  private var resourceExternalId: String? = null,
  private var resourceTypeSlug: String? = null
) {
  fun roleSlug(value: String) = apply { roleSlug = value }

  fun resourceId(value: String) = apply { resourceId = value }

  fun resourceExternalId(value: String) = apply { resourceExternalId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun build(): RemoveRoleOptions {
    return RemoveRoleOptions(
      roleSlug = this.roleSlug,
      resourceId = this.resourceId,
      resourceExternalId = this.resourceExternalId,
      resourceTypeSlug = this.resourceTypeSlug
    )
  }

  companion object {
    @JvmStatic
    fun create(roleSlug: String): RemoveRoleOptionsBuilder {
      return RemoveRoleOptionsBuilder(roleSlug)
    }
  }
}
