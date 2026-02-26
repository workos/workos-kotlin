package com.workos.authorization.builders

import com.workos.authorization.types.ListRoleAssignmentsOptions
import com.workos.common.models.Order

class ListRoleAssignmentsOptionsBuilder @JvmOverloads constructor(
  private var limit: Int? = null,
  private var order: Order? = null,
  private var before: String? = null,
  private var after: String? = null
) {
  fun limit(value: Int) = apply { limit = value }

  fun order(value: Order) = apply { order = value }

  fun before(value: String) = apply { before = value }

  fun after(value: String) = apply { after = value }

  fun build(): ListRoleAssignmentsOptions {
    return ListRoleAssignmentsOptions(
      limit = this.limit,
      order = this.order,
      before = this.before,
      after = this.after
    )
  }

  companion object {
    @JvmStatic
    fun create(): ListRoleAssignmentsOptionsBuilder {
      return ListRoleAssignmentsOptionsBuilder()
    }
  }
}
