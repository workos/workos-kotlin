package com.workos.common.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Contains cursor options to utilize for pagination.
 *
 * @param after The `after` cursor to use for retrieving the next page of data.
 * @param before The `before` cursor to use for retrieving the previous page of data.
 */
data class ListMetadata
@JsonCreator constructor(
  @JvmField
  val after: String?,

  @JvmField
  val before: String?
)
