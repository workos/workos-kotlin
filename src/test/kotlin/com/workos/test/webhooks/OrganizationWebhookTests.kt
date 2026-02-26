package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.OrganizationCreatedEvent
import com.workos.webhooks.models.OrganizationUpdatedEvent
import com.workos.webhooks.models.OrganizationDeletedEvent
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class OrganizationWebhookTests : TestBase() {

  private val organizationId = "org_01EHWNCE74X7JSDV0X3SZ3KJNY"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "organization",
        "id": "$organizationId",
        "name": "Test Organization",
        "allow_profiles_outside_organization": false,
        "domains": [
          {
            "object": "organization_domain",
            "id": "org_domain_01H7ZGXFP5C6BBQY6Z7277ZCT0",
            "domain": "example.com",
            "state": "verified",
            "verification_strategy": "dns",
            "verification_token": "abc123"
          }
        ],
        "created_at": "2023-11-27T19:07:33.155Z",
        "updated_at": "2023-11-27T19:07:33.155Z"
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """
  }

  @Test
  fun constructOrganizationCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationCreatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationCreatedEvent).data.id, organizationId)
    assertEquals(webhook.data.name, "Test Organization")
  }

  @Test
  fun constructOrganizationUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationUpdatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationUpdatedEvent).data.id, organizationId)
    assertEquals(webhook.data.name, "Test Organization")
  }

  @Test
  fun constructOrganizationDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDeletedEvent).data.id, organizationId)
    assertEquals(webhook.data.name, "Test Organization")
  }
}

