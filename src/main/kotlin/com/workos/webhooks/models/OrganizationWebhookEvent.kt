package com.workos.webhooks.models

import com.workos.organizations.models.Organization

/**
 * Webhook Event for `organization.*` events.
 */
class OrganizationWebhookEvent(
  override val id: String,
  override val event: EventType,
  override val data: Organization,
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
