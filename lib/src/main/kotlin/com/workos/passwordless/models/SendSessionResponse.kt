package com.workos.passwordless.models

import com.fasterxml.jackson.annotation.JsonCreator

data class SendSessionResponse @JsonCreator constructor(
  val success: Boolean
)
