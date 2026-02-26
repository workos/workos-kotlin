package com.workos.authorization.builders

import com.workos.authorization.types.ListAuthorizationResourcesOptions
import com.workos.common.models.Order

class ListAuthorizationResourcesOptionsBuilder @JvmOverloads constructor(
  private var organizationId: String? = null,
  private var resourceTypeSlug: String? = null,
  private var parentResourceId: String? = null,
  private var parentResourceTypeSlug: String? = null,
  private var parentExternalId: String? = null,
  private var search: String? = null,
  private var limit: Int? = null,
  private var order: Order? = null,
  private var before: String? = null,
  private var after: String? = null
) {
  fun organizationId(value: String) = apply { organizationId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun parentResourceId(value: String) = apply { parentResourceId = value }

  fun parentResourceTypeSlug(value: String) = apply { parentResourceTypeSlug = value }

  fun parentExternalId(value: String) = apply { parentExternalId = value }

  fun search(value: String) = apply { search = value }

  fun limit(value: Int) = apply { limit = value }

  fun order(value: Order) = apply { order = value }

  fun before(value: String) = apply { before = value }

  fun after(value: String) = apply { after = value }

  fun build(): ListAuthorizationResourcesOptions {
    return ListAuthorizationResourcesOptions(
      organizationId = this.organizationId,
      resourceTypeSlug = this.resourceTypeSlug,
      parentResourceId = this.parentResourceId,
      parentResourceTypeSlug = this.parentResourceTypeSlug,
      parentExternalId = this.parentExternalId,
      search = this.search,
      limit = this.limit,
      order = this.order,
      before = this.before,
      after = this.after
    )
  }

  companion object {
    @JvmStatic
    fun create(): ListAuthorizationResourcesOptionsBuilder {
      return ListAuthorizationResourcesOptionsBuilder()
    }
  }
}
