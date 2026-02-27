package com.workos.authorization.builders

import com.workos.authorization.types.ListResourcesForMembershipOptions
import com.workos.common.models.Order

class ListResourcesForMembershipOptionsBuilder @JvmOverloads constructor(
  private var organizationMembershipId: String,
  private var permissionSlug: String,
  private var parentResourceId: String? = null,
  private var parentResourceTypeSlug: String? = null,
  private var parentResourceExternalId: String? = null,
  private var limit: Int? = null,
  private var order: Order? = null,
  private var before: String? = null,
  private var after: String? = null
) {
  fun organizationMembershipId(value: String) = apply { organizationMembershipId = value }

  fun permissionSlug(value: String) = apply { permissionSlug = value }

  fun parentResourceId(value: String) = apply { parentResourceId = value }

  fun parentResourceTypeSlug(value: String) = apply { parentResourceTypeSlug = value }

  fun parentResourceExternalId(value: String) = apply { parentResourceExternalId = value }

  fun limit(value: Int) = apply { limit = value }

  fun order(value: Order) = apply { order = value }

  fun before(value: String) = apply { before = value }

  fun after(value: String) = apply { after = value }

  fun build(): ListResourcesForMembershipOptions {
    return ListResourcesForMembershipOptions(
      organizationMembershipId = this.organizationMembershipId,
      permissionSlug = this.permissionSlug,
      parentResourceId = this.parentResourceId,
      parentResourceTypeSlug = this.parentResourceTypeSlug,
      parentResourceExternalId = this.parentResourceExternalId,
      limit = this.limit,
      order = this.order,
      before = this.before,
      after = this.after
    )
  }

  companion object {
    @JvmStatic
    fun create(organizationMembershipId: String, permissionSlug: String): ListResourcesForMembershipOptionsBuilder {
      return ListResourcesForMembershipOptionsBuilder(organizationMembershipId, permissionSlug)
    }
  }
}
