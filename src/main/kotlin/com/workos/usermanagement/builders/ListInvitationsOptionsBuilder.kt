package com.workos.usermanagement.builders

import com.workos.common.models.Order
import com.workos.usermanagement.types.ListInvitationsOptions

/**
 * Builder for options when listing invitations.
 *
 * @param email Filter invitations by their email.
 * @param organizationId Filter invitations by the organization they are members of.
 * @param limit Maximum number of records to return.
 * @param before Pagination cursor to receive records before a provided invitations ID.
 * @param after Pagination cursor to receive records after a provided invitations ID.
 * @param order Sort records in either ascending or descending order by created_at timestamp: "asc" or "desc".
 */
class ListInvitationsOptionsBuilder @JvmOverloads constructor(
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
   * Generates the ListInvitations options.
   */
  fun build(): ListInvitationsOptions {
    return ListInvitationsOptions(
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
    fun create(): ListInvitationsOptionsBuilder {
      return ListInvitationsOptionsBuilder()
    }
  }
}
