package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.MagicAuthEvent
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class MagicAuthWebhookTests : TestBase() {

  private val magicAuthId = "magic_auth_01EHWNC0FCBHZ3BJ7EGKYXK0E7"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "magic_auth",
        "id": "$magicAuthId",
        "user_id": "user_01HWZBQAY251RZ9BKB4RZW4D4A",
        "email": "marcelina@foo-corp.com",
        "expires_at": "2023-11-27T19:07:33.155Z",
        "created_at": "2023-11-27T19:07:33.155Z",
        "updated_at": "2023-11-27T19:07:33.155Z"
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """
  }

  @Test
  fun constructMagicAuthCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.MagicAuthCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is MagicAuthEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as MagicAuthEvent).data.id, magicAuthId)
  }
}
