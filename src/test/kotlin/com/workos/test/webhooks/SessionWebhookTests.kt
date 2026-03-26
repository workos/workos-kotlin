package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class SessionWebhookTests : TestBase() {

  private val sessionId = "session_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"
  private val userId = "user_01FMXJ0YAP7JX3377YFV2XPCJE"

  private fun generateSessionWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "object": "session",
        "id": "$sessionId",
        "user_id": "$userId",
        "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0",
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructSessionCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateSessionWebhookEvent(EventType.SessionCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is SessionWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as SessionWebhookEvent).data.id, sessionId)
    assertEquals(webhook.data.userId, userId)
  }

  @Test
  fun constructSessionRevokedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateSessionWebhookEvent(EventType.SessionRevoked)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is SessionWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as SessionWebhookEvent).data.id, sessionId)
    assertEquals(webhook.data.userId, userId)
  }
}
