package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.InvitationEvent
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class InvitationWebhookTests : TestBase() {

  private val invitationId = "invitation_01EHWNC0FCBHZ3BJ7EGKYXK0E7"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "invitation",
        "id": "$invitationId",
        "email": "marcelina@foo-corp.com",
        "state": "pending",
        "accepted_at": null,
        "revoked_at": null,
        "expires_at": "2023-11-27T19:07:33.155Z",
        "organization_id": "org_01EHWNCE74X7JSDV0X3SZ3KJNY",
        "created_at": "2023-11-27T19:07:33.155Z",
        "updated_at": "2023-11-27T19:07:33.155Z"
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """
  }

  @Test
  fun constructInvitationCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.InvitationCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is InvitationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as InvitationEvent).data.id, invitationId)
  }
}
