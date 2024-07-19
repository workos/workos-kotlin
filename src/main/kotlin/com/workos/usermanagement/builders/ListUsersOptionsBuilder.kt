package com.workos.usermanagement.builders

import com.workos.common.models.Order
import com.workos.usermanagement.types.ListUsersOptions

/**
 * Builder for options when listing users.
 *
 * @param email Filter users by their email.
 * @param organizationId Filter users by the organization they are members of.
 * @param limit Maximum number of records to return.
 * @param before Pagination cursor to receive records before a provided user ID.
 * @param after Pagination cursor to receive records after a provided user ID.
 * @param order Sort records in either ascending or descending order by created_at timestamp: "asc" or "desc".
 */
class ListUsersOptionsBuilder @JvmOverloads constructor(
  private var email: String? = null,
  private var organizationId: String? = null,
  private var limit: Int? = null,
  private var before: String? = null,
  private var after: String? = null,
  private var order: Order? = null,
) {
  /**
   * Email
   */
  fun email(value: String) = apply { email = value }

  /**
   * Organization Id
   */
  fun organizationId(value: String) = apply { organizationId = value }

  /**
   * Limit
   */
  fun limit(value: Int) = apply { limit = value }

  /**
   * Before
   */
  fun before(value: String) = apply { before = value }

  /**
   * After
   */
  fun after(value: String) = apply { after = value }

  /**
   * Sorting Order
   */
  fun order(value: Order) = apply { order = value }

  /**
   * Generates the ListUsers options.
   */
  fun build(): ListUsersOptions {
    return ListUsersOptions(
      email = this.email,
      organizationId = this.organizationId,
      limit = this.limit,
      before = this.before,
      after = this.after,
      order = this.order,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(): ListUsersOptionsBuilder {
      return ListUsersOptionsBuilder()
    }
  }
}
