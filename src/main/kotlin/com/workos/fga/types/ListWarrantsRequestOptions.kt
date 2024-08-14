package com.workos.fga.types

class ListWarrantsRequestOptions constructor(
  /**
   * A valid token string from a previous write operation or latest. This is used to specify the desired consistency for this read operation.
   */
  val warrantToken: String? = null,
)
