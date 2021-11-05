package com.workos.common.http

/**
 * Parameters for performing paginated requests on lists of resources.
 *
 * @param after A cursor to use for pagination. `after` is a resource object ID that defines your place in the paginated list of resource objects. `after` will return all resources after the cursor's value.
 * @param before A cursor to use for pagination. `before` is a resource object ID that defines your place in the paginated list of resource objects. `before` will return all resources before the cursor's value.
 * @param limit Upper limit on the number of resources to return, between 1 and 100. The default value is 10.
 */
open class PaginationParams @JvmOverloads constructor(
  after: String? = null,
  before: String? = null,
  limit: Int? = null,
) : HashMap<String, String>() {

  init {
    if (after != null) {
      set("after", after)
    }

    if (before != null) {
      set("before", before)
    }

    if (limit != null) {
      set("limit", limit.toString())
    }
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun builder(): PaginationParamsBuilder<PaginationParams> {
      return PaginationParamsBuilder(PaginationParams())
    }
  }

  /**
   * Builder class for creating [PaginationParams].
   */
  open class PaginationParamsBuilder<T : PaginationParams>(protected val params: T) {
    /**
     * Sets the `after` query parameter.
     */
    fun after(after: String) = apply { this.params["after"] = after }
    /**
     * Sets the `before` query parameter.
     */
    fun before(before: String) = apply { this.params["before"] = before }

    /**
     * Sets the `limit` query parameter.
     */
    fun limit(limit: Int) = apply { this.params["limit"] = limit.toString() }

    /**
     * Creates an instance of T with the given builder parameters.
     */
    open fun build(): T {
      return params
    }
  }
}
