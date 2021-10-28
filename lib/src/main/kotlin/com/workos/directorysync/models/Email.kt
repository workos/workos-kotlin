package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator

data class Email
@JsonCreator constructor(
  val primary: Boolean,

  val type: String,

  val value: String,
)
