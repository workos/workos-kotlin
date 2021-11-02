package com.workos.common.models

import com.fasterxml.jackson.annotation.JsonCreator

data class ListMetadata
@JsonCreator constructor(
  @JvmField
  val after: String?,

  @JvmField
  val before: String?
)
