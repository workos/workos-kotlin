package com.workos.common.models

/**
 * An enumeration for the orders for pagination.
 */
enum class ConnectionType(val type: String) {
  /**
   * Ascending Order
   */
  Asc("asc"),
  /**
   * Descending Order
   */
  Desc("desc"),
}
