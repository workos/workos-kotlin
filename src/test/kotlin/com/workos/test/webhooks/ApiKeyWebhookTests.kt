package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class ApiKeyWebhookTests : TestBase() {

  private val apiKeyId = "api_key_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateApiKeyWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "object": "api_key",
        "id": "$apiKeyId",
        "name": "Production API Key",
        "obfuscated_value": "sk_...abc123",
        "last_used_at": null,
        "permissions": ["read", "write"],
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructApiKeyCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateApiKeyWebhookEvent(EventType.ApiKeyCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is ApiKeyWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ApiKeyWebhookEvent).data.id, apiKeyId)
    assertEquals(webhook.data.name, "Production API Key")
  }

  @Test
  fun constructApiKeyRevokedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateApiKeyWebhookEvent(EventType.ApiKeyRevoked)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is ApiKeyWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ApiKeyWebhookEvent).data.id, apiKeyId)
  }
}
