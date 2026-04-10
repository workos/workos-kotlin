@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.*
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class OrganizationRoleWebhookTests : TestBase() {
  private val roleSlug = "admin"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateOrganizationRoleWebhookEvent(eventType: EventType): String =
    """
    {
      "id": "$webhookId",
      "data": {
        "object": "organization_role",
        "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8",
        "slug": "$roleSlug",
        "name": "Admin",
        "description": "Administrator role",
        "resource_type_slug": "organization",
        "permissions": ["admin:manage", "admin:read"],
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """

  @Test
  fun constructOrganizationRoleCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationRoleWebhookEvent(EventType.OrganizationRoleCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationRoleWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationRoleWebhookEvent).data.slug, roleSlug)
    assertEquals(webhook.data.name, "Admin")
  }

  @Test
  fun constructOrganizationRoleDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationRoleWebhookEvent(EventType.OrganizationRoleDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationRoleWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationRoleWebhookEvent).data.slug, roleSlug)
  }

  @Test
  fun constructOrganizationRoleUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationRoleWebhookEvent(EventType.OrganizationRoleUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationRoleWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationRoleWebhookEvent).data.slug, roleSlug)
  }
}
