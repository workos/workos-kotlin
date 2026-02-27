package com.workos.authorization.builders

import com.workos.authorization.types.AssignRoleOptions

class AssignRoleOptionsBuilder @JvmOverloads constructor(
  private var organizationMembershipId: String,
  private var roleSlug: String,
  private var resourceId: String? = null,
  private var resourceExternalId: String? = null,
  private var resourceTypeSlug: String? = null
) {
  fun organizationMembershipId(value: String) = apply { organizationMembershipId = value }

  fun roleSlug(value: String) = apply { roleSlug = value }

  fun resourceId(value: String) = apply { resourceId = value }

  fun resourceExternalId(value: String) = apply { resourceExternalId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun build(): AssignRoleOptions {
    return AssignRoleOptions(
      organizationMembershipId = this.organizationMembershipId,
      roleSlug = this.roleSlug,
      resourceId = this.resourceId,
      resourceExternalId = this.resourceExternalId,
      resourceTypeSlug = this.resourceTypeSlug
    )
  }

  companion object {
    @JvmStatic
    fun create(organizationMembershipId: String, roleSlug: String): AssignRoleOptionsBuilder {
      return AssignRoleOptionsBuilder(organizationMembershipId, roleSlug)
    }
  }
}
