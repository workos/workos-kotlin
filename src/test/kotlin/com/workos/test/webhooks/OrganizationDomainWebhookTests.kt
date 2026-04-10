package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.EventType
import com.workos.webhooks.models.OrganizationDomainEvent
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class OrganizationDomainWebhookTests : TestBase() {
  private val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
  private val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
  private val webhookId = "wh_01FMXJ2W7T9VY7EAHHMBF2K07Y"

  private fun generateWebhookEvent(
    eventType: EventType,
    state: String = "verified"
  ): String =
    """
    {
      "id": "$webhookId",
      "event": "${eventType.value}",
      "data": {
        "object": "organization_domain",
        "id": "$organizationDomainId",
        "organization_id": "$organizationId",
        "domain": "example.com",
        "state": "$state",
        "verification_strategy": "dns",
        "verification_token": "rqURsMUCuiaSggGyed8ZAnMk"
      },
      "created_at": "2023-11-27T19:07:33.155Z"
    }
    """

  @Test
  fun constructOrganizationDomainCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationDomainCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationDomainEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainEvent).data.id, organizationDomainId)
    assertEquals(webhook.data.organizationId, organizationId)
    assertEquals(webhook.data.domain, "example.com")
  }

  @Test
  fun constructOrganizationDomainUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationDomainUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationDomainEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainEvent).data.id, organizationDomainId)
    assertEquals(webhook.data.organizationId, organizationId)
  }

  @Test
  fun constructOrganizationDomainDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationDomainDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationDomainEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainEvent).data.id, organizationDomainId)
    assertEquals(webhook.data.organizationId, organizationId)
  }

  @Test
  fun constructOrganizationDomainVerifiedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationDomainVerified)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationDomainEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainEvent).data.id, organizationDomainId)
    assertEquals(webhook.data.organizationId, organizationId)
    assertEquals(webhook.data.domain, "example.com")
    assertEquals(webhook.data.verificationToken, "rqURsMUCuiaSggGyed8ZAnMk")
  }

  @Test
  fun constructOrganizationDomainVerificationFailedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateWebhookEvent(EventType.OrganizationDomainVerificationFailed, "failed")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is OrganizationDomainEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as OrganizationDomainEvent).data.id, organizationDomainId)
    assertEquals(webhook.data.organizationId, organizationId)
  }
}
