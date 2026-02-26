package com.workos.authorization.builders

import com.workos.authorization.types.AssignmentFilter
import com.workos.authorization.types.ListMembershipsForResourceOptions
import com.workos.common.models.Order

class ListMembershipsForResourceOptionsBuilder @JvmOverloads constructor(
  private var permissionSlug: String,
  private var assignment: AssignmentFilter? = null,
  private var limit: Int? = null,
  private var order: Order? = null,
  private var before: String? = null,
  private var after: String? = null
) {
  fun permissionSlug(value: String) = apply { permissionSlug = value }

  fun assignment(value: AssignmentFilter) = apply { assignment = value }

  fun limit(value: Int) = apply { limit = value }

  fun order(value: Order) = apply { order = value }

  fun before(value: String) = apply { before = value }

  fun after(value: String) = apply { after = value }

  fun build(): ListMembershipsForResourceOptions {
    return ListMembershipsForResourceOptions(
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
    fun create(permissionSlug: String): ListMembershipsForResourceOptionsBuilder {
      return ListMembershipsForResourceOptionsBuilder(permissionSlug)
    }
  }
}
