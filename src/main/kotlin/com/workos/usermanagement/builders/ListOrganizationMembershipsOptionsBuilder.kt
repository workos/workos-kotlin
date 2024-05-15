package com.workos.usermanagement.builders

import com.workos.common.models.Order
import com.workos.usermanagement.types.ListOrganizationMembershipsOptions
import com.workos.usermanagement.types.OrganizationMembershipStatusEnumType

/**
 * Builder for options when listing organization memberships.
 *
 * @param userId Filter organization memberships by their id.
 * @param organizationId Filter organization memberships by the organization they are members of.
 * @param statuses Filter organization memberships by the membership status.
 * @param limit Maximum number of records to return.
 * @param before Pagination cursor to receive records before a provided organization membership ID.
 * @param after Pagination cursor to receive records after a provided organization membership ID.
 * @param order Sort records in either ascending or descending order by created_at timestamp: "asc" or "desc".
 */
class ListOrganizationMembershipsOptionsBuilder @JvmOverloads constructor(
  private var userId: String? = null,
  private var organizationId: String? = null,
  private var statuses: List<OrganizationMembershipStatusEnumType>? = null,
  private var limit: Int? = null,
  private var before: String? = null,
  private var after: String? = null,
  private var order: Order? = null,
) {
  /**
   * User Id
   */
  fun userId(value: String) = apply { userId = value }

  /**
   * Organization Id
   */
  fun organizationId(value: String) = apply { organizationId = value }

  /**
   * Statuses
   */
  fun statuses(value: List<OrganizationMembershipStatusEnumType>) = apply { statuses = value }

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
   * Generates the ListOrganizationMembershipsOptions object.
   */
  fun build(): ListOrganizationMembershipsOptions {
    return ListOrganizationMembershipsOptions(
      userId = this.userId,
      organizationId = this.organizationId,
      statuses = this.statuses,
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
    fun create(): ListOrganizationMembershipsOptionsBuilder {
      return ListOrganizationMembershipsOptionsBuilder()
    }
  }
}
