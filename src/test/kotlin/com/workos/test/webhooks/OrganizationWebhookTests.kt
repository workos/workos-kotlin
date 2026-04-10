@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.*
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class OrganizationWebhookTests : TestBase() {
  private val organizationId = "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateOrganizationWebhookEvent(eventType: EventType): String =
    """
    {
      "id": "$webhookId",
      "data": {
        "object": "organization",
        "id": "$organizationId",
        "name": "Foo Corp",
        "allow_profiles_outside_organization": false,
        "domains": [],
        "created_at": "2024-07-20T10:15:23.713Z",
        "updated_at": "2024-07-20T10:15:23.713Z"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """

  @Test
  fun constructOrganizationCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationWebhookEvent(EventType.OrganizationCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationWebhookEvent).data.id, organizationId)
    assertEquals(webhook.data.name, "Foo Corp")
  }

  @Test
  fun constructOrganizationDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationWebhookEvent(EventType.OrganizationDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationWebhookEvent).data.id, organizationId)
  }

  @Test
  fun constructOrganizationUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationWebhookEvent(EventType.OrganizationUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationWebhookEvent).data.id, organizationId)
  }
}
