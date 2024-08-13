package com.workos.fga.types

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.models.Order

@JsonInclude(JsonInclude.Include.NON_NULL)
class QueryOptions @JvmOverloads constructor(
  /**
   * Query to be executed.
   */
  @JsonProperty("q")
  val query: String,

  /**
   * Contextual data to use for resolving the access check. This data will be used when evaluating warrant policies.
   */
  @JsonProperty("context")
  val context: Map<String, Any>? = null,

  @JsonProperty("limit")
  val limit: Int? = null,

  @JsonProperty("order")
  val order: Order? = null,

  @JsonProperty("before")
  val before: String? = null,

  @JsonProperty("after")
  val after: String? = null
) {
  init {
    require(query.isNotBlank()) { "Query is required" }
  }
}
