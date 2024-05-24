package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EmailVerificationEvent
import com.workos.webhooks.models.EventType
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class EmailVerificationWebhookTests : TestBase() {

  private val emailVerificationId = "email_verification_01EHWNC0FCBHZ3BJ7EGKYXK0E7"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "email_verification",
        "id": "$emailVerificationId",
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
  fun constructEmailVerificationCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.EmailVerificationCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is EmailVerificationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as EmailVerificationEvent).data.id, emailVerificationId)
  }
}
