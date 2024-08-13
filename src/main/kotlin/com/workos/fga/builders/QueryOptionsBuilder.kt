package com.workos.fga.builders

import com.workos.common.models.Order
import com.workos.fga.types.QueryOptions

/**
 * Builder for options when making a query.
 *
 * @param query Query to be executed.
 * @param context Contextual data to use for the query.
 * @param limit Maximum number of records to return.
 * @param before Pagination cursor to receive records before a provided resource ID.
 * @param after Pagination cursor to receive records after a provided resource ID.
 * @param order Sort records in either ascending or descending order by created_at timestamp: "asc" or "desc".
 */
class QueryOptionsBuilder @JvmOverloads constructor(
  private var query: String,
  private var context: Map<String, Any>? = null,
  private var limit: Int? = null,
  private var before: String? = null,
  private var after: String? = null,
  private var order: Order? = null,
) {
  /**
   * Query
   */
  fun query(value: String) = apply { query = value }

  /**
   * Context
   */
  fun context(value: Map<String, Any>) = apply { context = value }

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
  fun build(): QueryOptions {
    return QueryOptions(
      query = this.query,
      context = this.context,
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
    fun create(query: String): QueryOptionsBuilder {
      return QueryOptionsBuilder(query)
    }
  }
}
