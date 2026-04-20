// @oagen-ignore-file
package com.workos.common.http

/**
 * Generic page wrapper returned by every list endpoint.
 *
 * [autoPagingIterable] walks the cursor chain on demand, issuing follow-up
 * requests through [nextPageFetcher]. Consumers iterate once and get every
 * item across every page without managing cursors themselves.
 */
class Page<T> internal constructor(
  /** Items on this page. */
  @JvmField val data: List<T>,
  /** Pagination cursors for fetching adjacent pages. */
  @JvmField val listMetadata: ListMetadata,
  private val nextPageFetcher: ((after: String) -> Page<T>)?
) {
  /** Whether there is at least one more page available after this one. */
  fun hasNextPage(): Boolean = listMetadata.after != null && nextPageFetcher != null

  /** Fetch and return the next page, or `null` if there is no more data. */
  fun nextPage(): Page<T>? {
    val after = listMetadata.after ?: return null
    val fetcher = nextPageFetcher ?: return null
    return fetcher(after)
  }

  /**
   * Iterate every item on every page, fetching follow-up pages lazily.
   * Safe to consume once — produces a fresh [Iterator] each time it's called,
   * and each iterator walks the cursor chain from `this` forward.
   */
  fun autoPagingIterable(): Iterable<T> =
    Iterable {
      iterator {
        var current: Page<T>? = this@Page
        while (current != null) {
          yieldAll(current.data)
          current = current.nextPage()
        }
      }
    }

  /** Factory methods for constructing [Page] instances outside of [BaseClient]. */
  companion object {
    /** Create a [Page] from pre-fetched data, an optional [nextPageFetcher] for auto-pagination. */
    @JvmStatic
    fun <T> of(
      data: List<T>,
      listMetadata: ListMetadata,
      nextPageFetcher: ((after: String) -> Page<T>)? = null
    ): Page<T> = Page(data, listMetadata, nextPageFetcher)
  }
}
