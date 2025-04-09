package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.PasswordResetEvent
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class PasswordResetWebhookTests : TestBase() {

  private val passwordResetId = "password_reset_01EHWNC0FCBHZ3BJ7EGKYXK0E7"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "password_reset",
        "id": "$passwordResetId",
        "user_id": "user_01HWZBQAY251RZ9BKB4RZW4D4A",
        "email": "marcelina@foo-corp.com",
        "expires_at": "2023-11-27T19:07:33.155Z",
        "created_at": "2023-11-27T19:07:33.155Z"
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """
  }

  @Test
  fun constructPasswordResetCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.PasswordResetCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is PasswordResetEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as PasswordResetEvent).data.id, passwordResetId)
  }

  fun constructPasswordResetSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.PasswordResetSucceeded)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is PasswordResetEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as PasswordResetEvent).data.id, passwordResetId)
  }
}
