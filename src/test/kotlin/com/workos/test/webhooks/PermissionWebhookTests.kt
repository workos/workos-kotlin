@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.*
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class PermissionWebhookTests : TestBase() {
  private val permissionId = "perm_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generatePermissionWebhookEvent(eventType: EventType): String =
    """
    {
      "id": "$webhookId",
      "data": {
        "object": "permission",
        "id": "$permissionId",
        "slug": "admin:manage",
        "name": "Admin Manage",
        "description": "Full admin access",
        "system": false,
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """

  @Test
  fun constructPermissionCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generatePermissionWebhookEvent(EventType.PermissionCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is PermissionWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as PermissionWebhookEvent).data.id, permissionId)
    assertEquals(webhook.data.slug, "admin:manage")
  }

  @Test
  fun constructPermissionDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generatePermissionWebhookEvent(EventType.PermissionDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is PermissionWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as PermissionWebhookEvent).data.id, permissionId)
  }

  @Test
  fun constructPermissionUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generatePermissionWebhookEvent(EventType.PermissionUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is PermissionWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as PermissionWebhookEvent).data.id, permissionId)
  }
}
