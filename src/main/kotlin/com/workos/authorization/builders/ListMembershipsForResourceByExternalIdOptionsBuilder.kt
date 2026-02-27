package com.workos.authorization.builders

import com.workos.authorization.types.Assignment
import com.workos.authorization.types.ListMembershipsForResourceByExternalIdOptions
import com.workos.common.models.Order

class ListMembershipsForResourceByExternalIdOptionsBuilder @JvmOverloads constructor(
  private var organizationId: String,
  private var resourceTypeSlug: String,
  private var externalId: String,
  private var permissionSlug: String,
  private var assignment: Assignment? = null,
  private var limit: Int? = null,
  private var order: Order? = null,
  private var before: String? = null,
  private var after: String? = null
) {
  fun organizationId(value: String) = apply { organizationId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun externalId(value: String) = apply { externalId = value }

  fun permissionSlug(value: String) = apply { permissionSlug = value }

  fun assignment(value: Assignment) = apply { assignment = value }

  fun limit(value: Int) = apply { limit = value }

  fun order(value: Order) = apply { order = value }

  fun before(value: String) = apply { before = value }

  fun after(value: String) = apply { after = value }

  fun build(): ListMembershipsForResourceByExternalIdOptions {
    return ListMembershipsForResourceByExternalIdOptions(
      organizationId = this.organizationId,
      resourceTypeSlug = this.resourceTypeSlug,
      externalId = this.externalId,
      permissionSlug = this.permissionSlug,
      assignment = this.assignment,
      limit = this.limit,
      order = this.order,
      before = this.before,
      after = this.after
    )
  }

  companion object {
    @JvmStatic
    fun create(
      organizationId: String,
      resourceTypeSlug: String,
      externalId: String,
      permissionSlug: String
    ): ListMembershipsForResourceByExternalIdOptionsBuilder {
      return ListMembershipsForResourceByExternalIdOptionsBuilder(
        organizationId,
        resourceTypeSlug,
        externalId,
        permissionSlug
      )
    }
  }
}
