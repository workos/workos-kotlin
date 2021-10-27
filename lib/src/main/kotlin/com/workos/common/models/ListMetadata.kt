package com.workos.common.models

import com.fasterxml.jackson.annotation.JsonCreator

data class ListMetadata
@JsonCreator constructor(
  val after: String?,
  val before: String?
)
