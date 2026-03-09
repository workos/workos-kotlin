package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.UserCreatedEvent
import com.workos.webhooks.models.UserDeletedEvent
import com.workos.webhooks.models.UserUpdatedEvent
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class UserWebhookTests : TestBase() {

  private val userId = "user_01HWZBQAY251RZ9BKB4RZW4D4A"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "id": "$userId",
        "email": "marcelina@foo-corp.com",
        "first_name": "Marcelina",
        "last_name": "Davis",
        "email_verified": true,
        "profile_picture_url": "https://example.com/profile.jpg",
        "last_sign_in_at": "2023-11-27T18:00:00.000Z",
        "created_at": "2023-11-27T19:07:33.155Z",
        "updated_at": "2023-11-27T19:07:33.155Z",
        "external_id": null,
        "metadata": {}
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """
  }

  @Test
  fun constructUserCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.UserCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is UserCreatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as UserCreatedEvent).data.id, userId)
  }

  @Test
  fun constructUserUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.UserUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is UserUpdatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as UserUpdatedEvent).data.id, userId)
  }

  @Test
  fun constructUserDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.UserDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is UserDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as UserDeletedEvent).data.id, userId)
  }
}
