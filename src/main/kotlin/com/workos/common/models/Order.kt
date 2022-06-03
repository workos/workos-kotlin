package com.workos.common.models

/**
 * An enumeration for the orders for pagination.
 */
enum class Order(val type: String) {
  /**
   * Ascending Order
   */
  Asc("asc"),
  /**
   * Descending Order
   */
  Desc("desc"),
}
