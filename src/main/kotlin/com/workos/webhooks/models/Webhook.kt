package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Represents a Webhook resource. This class is not meant to be
 * instantiated directly.
 *
 * @param id The unique identifier for the Webhook.
 * @param event String identifier for the Webhook type.
 * @param data Data sent with the Webhook request.
 */
@JsonDeserialize(using = WebhookJsonDeserializer::class)
data class Webhook
@JsonCreator constructor(
  @JvmField
  val id: String,

  @JvmField
  val event: EventType,

  @JvmField
  val data: Any
)
