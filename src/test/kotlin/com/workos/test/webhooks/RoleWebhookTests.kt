package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class RoleWebhookTests : TestBase() {

  private val roleId = "role_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateRoleWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "object": "role",
        "id": "$roleId",
        "name": "Admin",
        "slug": "admin",
        "description": "Administrator role",
        "type": "EnvironmentRole",
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructRoleCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateRoleWebhookEvent(EventType.RoleCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is RoleWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as RoleWebhookEvent).data.id, roleId)
    assertEquals(webhook.data.name, "Admin")
  }

  @Test
  fun constructRoleDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateRoleWebhookEvent(EventType.RoleDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is RoleWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as RoleWebhookEvent).data.id, roleId)
  }

  @Test
  fun constructRoleUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateRoleWebhookEvent(EventType.RoleUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is RoleWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as RoleWebhookEvent).data.id, roleId)
  }
}
