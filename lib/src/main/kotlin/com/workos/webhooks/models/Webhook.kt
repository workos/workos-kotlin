package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator

data class Webhook
@JsonCreator constructor(
  val id: String,

  val event: String,

  val data: Map<String, Any>
)
