package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator

data class Email
@JsonCreator constructor(
  @JvmField
  val primary: Boolean?,

  @JvmField
  val type: String?,

  @JvmField
  val value: String?,
)
