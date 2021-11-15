package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Represents a Webhook resource. This class is not meant to be
 * instantiated directly.
 *
 * @param id The unique identifier for the Webhook.
 * @param event String identifier for the Webhook type.
 * @param data Data sent with the Webhook request.
 */
data class Webhook
@JsonCreator constructor(
  @JvmField
  val id: String,

  @JvmField
  val event: String,

  @JvmField
  val data: Map<String, Any>
)
