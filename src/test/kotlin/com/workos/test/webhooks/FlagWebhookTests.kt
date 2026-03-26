package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class FlagWebhookTests : TestBase() {

  private val flagId = "flag_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateFlagWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "object": "feature_flag",
        "id": "$flagId",
        "environment_id": "env_01FKPWZWPHE9VN2QXJ7G1BZYP8",
        "slug": "new-feature",
        "name": "New Feature",
        "description": "A new feature flag",
        "tags": ["beta"],
        "enabled": true,
        "default_value": false,
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructFlagCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateFlagWebhookEvent(EventType.FlagCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is FlagWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as FlagWebhookEvent).data.id, flagId)
    assertEquals(webhook.data.slug, "new-feature")
  }

  @Test
  fun constructFlagDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateFlagWebhookEvent(EventType.FlagDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is FlagWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as FlagWebhookEvent).data.id, flagId)
  }

  @Test
  fun constructFlagUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateFlagWebhookEvent(EventType.FlagUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is FlagWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as FlagWebhookEvent).data.id, flagId)
  }

  @Test
  fun constructFlagRuleUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateFlagWebhookEvent(EventType.FlagRuleUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is FlagWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as FlagWebhookEvent).data.id, flagId)
  }
}
