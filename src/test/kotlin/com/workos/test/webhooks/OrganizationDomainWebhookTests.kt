package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class OrganizationDomainWebhookTests : TestBase() {

  private val domainId = "org_domain_01FKPWZWPHE9VN2QXJ7G1BZYP8"
  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateOrganizationDomainWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "object": "organization_domain",
        "id": "$domainId",
        "domain": "foo-corp.com",
        "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8",
        "state": "verified"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructOrganizationDomainCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationDomainWebhookEvent(EventType.OrganizationDomainCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationDomainWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainWebhookEvent).data.id, domainId)
  }

  @Test
  fun constructOrganizationDomainDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationDomainWebhookEvent(EventType.OrganizationDomainDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationDomainWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainWebhookEvent).data.id, domainId)
  }

  @Test
  fun constructOrganizationDomainUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationDomainWebhookEvent(EventType.OrganizationDomainUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationDomainWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainWebhookEvent).data.id, domainId)
  }

  @Test
  fun constructOrganizationDomainVerifiedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationDomainWebhookEvent(EventType.OrganizationDomainVerified)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationDomainWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainWebhookEvent).data.id, domainId)
  }

  @Test
  fun constructOrganizationDomainVerificationFailedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateOrganizationDomainWebhookEvent(EventType.OrganizationDomainVerificationFailed)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is OrganizationDomainWebhookEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainWebhookEvent).data.id, domainId)
  }
}
