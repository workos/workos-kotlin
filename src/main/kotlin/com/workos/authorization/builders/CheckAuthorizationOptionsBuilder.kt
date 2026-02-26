package com.workos.authorization.builders

import com.workos.authorization.types.CheckAuthorizationOptions

class CheckAuthorizationOptionsBuilder @JvmOverloads constructor(
  private var permissionSlug: String,
  private var resourceId: String? = null,
  private var resourceExternalId: String? = null,
  private var resourceTypeSlug: String? = null
) {
  fun permissionSlug(value: String) = apply { permissionSlug = value }

  fun resourceId(value: String) = apply { resourceId = value }

  fun resourceExternalId(value: String) = apply { resourceExternalId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun build(): CheckAuthorizationOptions {
    return CheckAuthorizationOptions(
      permissionSlug = this.permissionSlug,
      resourceId = this.resourceId,
      resourceExternalId = this.resourceExternalId,
      resourceTypeSlug = this.resourceTypeSlug
    )
  }

  companion object {
    @JvmStatic
    fun create(permissionSlug: String): CheckAuthorizationOptionsBuilder {
      return CheckAuthorizationOptionsBuilder(permissionSlug)
    }
  }
}
