package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Represents a Webhook resource. This the base class for each
 * type of Webhook.
 *
 * @param id The unique identifier for the Webhook.
 * @param event String identifier for the Webhook type.
 * @param data Data sent with the Webhook request.
 */
@JsonDeserialize(using = WebhookJsonDeserializer::class)
abstract class WebhookEvent
@JsonCreator constructor(
  @JvmField
  open val id: String,

  @JvmField
  open val event: EventType,

  @JvmField
  open val data: Any,

  @JvmField
  override val createdAt: String
)
