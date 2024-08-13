package com.workos.fga.builders

import com.workos.common.models.Order
import com.workos.fga.types.ListResourcesOptions

/**
 * Builder for options when listing resources.
 *
 * @param resourceType Filter resources by their type.
 * @param search Returns resources where resource_type or resource_id contains the search term.
 * @param limit Maximum number of records to return.
 * @param before Pagination cursor to receive records before a provided resource ID.
 * @param after Pagination cursor to receive records after a provided resource ID.
 * @param order Sort records in either ascending or descending order by created_at timestamp: "asc" or "desc".
 */
class ListResourcesOptionsBuilder @JvmOverloads constructor(
  private var resourceType: String? = null,
  private var search: String? = null,
  private var limit: Int? = null,
  private var before: String? = null,
  private var after: String? = null,
  private var order: Order? = null,
) {
  /**
   * Resource Type
   */
  fun resourceType(value: String) = apply { resourceType = value }

  /**
   * Search
   */
  fun search(value: String) = apply { search = value }

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
   * Generates the ListResources options.
   */
  fun build(): ListResourcesOptions {
    return ListResourcesOptions(
      resourceType = this.resourceType,
      search = this.search,
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
    fun create(): ListResourcesOptionsBuilder {
      return ListResourcesOptionsBuilder()
    }
  }
}
