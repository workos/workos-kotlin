package com.workos.webhooks.models

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
abstract class WebhookEvent(
  open val id: String,
  open val event: EventType,
  open val data: Any,
  open val createdAt: String
)
