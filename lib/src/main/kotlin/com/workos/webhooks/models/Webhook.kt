package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator

data class Webhook
@JsonCreator constructor(
  @JvmField
  val id: String,

  @JvmField
  val event: String,

  @JvmField
  val data: Map<String, Any>
)
