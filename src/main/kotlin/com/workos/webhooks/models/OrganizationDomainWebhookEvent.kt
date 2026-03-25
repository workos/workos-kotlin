package com.workos.webhooks.models

import com.workos.organizations.models.OrganizationDomain

/**
 * Webhook Event for `organization_domain.*` events.
 */
class OrganizationDomainWebhookEvent(
  @JvmField
  override val id: String,

  @JvmField
  override val event: EventType,

  @JvmField
  override val data: OrganizationDomain,

  @JvmField
  override val createdAt: String
) : WebhookEvent(id, event, data, createdAt)
