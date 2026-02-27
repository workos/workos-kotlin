package com.workos.authorization.builders

import com.workos.authorization.types.CheckAuthorizationOptions

class CheckAuthorizationOptionsBuilder @JvmOverloads constructor(
  private var organizationMembershipId: String,
  private var permissionSlug: String,
  private var resourceId: String? = null,
  private var resourceExternalId: String? = null,
  private var resourceTypeSlug: String? = null
) {
  fun organizationMembershipId(value: String) = apply { organizationMembershipId = value }

  fun permissionSlug(value: String) = apply { permissionSlug = value }

  fun resourceId(value: String) = apply { resourceId = value }

  fun resourceExternalId(value: String) = apply { resourceExternalId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun build(): CheckAuthorizationOptions {
    return CheckAuthorizationOptions(
      organizationMembershipId = this.organizationMembershipId,
      permissionSlug = this.permissionSlug,
      resourceId = this.resourceId,
      resourceExternalId = this.resourceExternalId,
      resourceTypeSlug = this.resourceTypeSlug
    )
  }

  companion object {
    @JvmStatic
    fun create(organizationMembershipId: String, permissionSlug: String): CheckAuthorizationOptionsBuilder {
      return CheckAuthorizationOptionsBuilder(organizationMembershipId, permissionSlug)
    }
  }
}
