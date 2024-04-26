package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.OrganizationMembershipEvent
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class OrganizationMembershipWebhookTests : TestBase() {

  private val organizationMembershipId = "om_01EHWNC0FCBHZ3BJ7EGKYXK0E7"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "organization_membership",
        "id": "$organizationMembershipId",
        "user_id": "user_01EHWNC0FCBHZ3BJ7EGKYXK0E6",
        "organization_id": "org_01EHWNCE74X7JSDV0X3SZ3KJNY",
        "status": "active",
        "created_at": "2023-11-27T19:07:33.155Z",
        "updated_at": "2023-11-27T19:07:33.155Z"
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """
  }

  @Test
  fun constructOrganizationMembershipCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationMembershipCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationMembershipEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationMembershipEvent).data.id, organizationMembershipId)
  }

  @Test
  fun constructOrganizationMembershipDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationMembershipDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationMembershipEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationMembershipEvent).data.id, organizationMembershipId)
  }

  @Test
  fun constructOrganizationMembershipUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationMembershipUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationMembershipEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationMembershipEvent).data.id, organizationMembershipId)
  }
}
