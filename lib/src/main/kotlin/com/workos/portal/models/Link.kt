package com.workos.portal.models

import com.fasterxml.jackson.annotation.JsonCreator

data class Link
@JsonCreator constructor(
  @JvmField
  val link: String
)
