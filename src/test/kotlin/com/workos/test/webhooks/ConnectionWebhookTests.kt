@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.*
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class ConnectionWebhookTests : TestBase() {
  private val connectionId = "conn_01FMXJ0YAP7JX3377YFV2XPCJE"

  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateGroupWebhookEvent(eventType: EventType): String =
    """
    {
      "id": "$webhookId",
      "data": {
        "id": "$connectionId",
        "name": "G",
        "state": "inactive",
        "object": "connection",
        "created_at": "2021-11-20T10:15:23.713Z",
        "updated_at": "2021-11-20T10:15:23.713Z",
        "connection_type": "GoogleSAML",
        "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
      },
      "event": "${eventType.value}",
      "created_at": "2021-11-20T10:15:23.713Z"
    }
    """

  @Test
  fun constructDirectoryActivatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.ConnectionActivated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is ConnectionActivatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionActivatedEvent).data.id, connectionId)
  }

  @Test
  fun constructDirectoryDirectoryDeactivatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.ConnectionDeactivated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is ConnectionDeactivatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionDeactivatedEvent).data.id, connectionId)
  }

  @Test
  fun constructDirectoryDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.ConnectionDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is ConnectionDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionDeletedEvent).data.id, connectionId)
  }

  @Test
  fun constructConnectionSamlCertificateRenewalRequiredEvent() {
    val workos = createWorkOSClient()
    val webhookData = """
    {
      "id": "$webhookId",
      "data": {
        "connection": {
          "id": "$connectionId",
          "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
        },
        "certificate": {
          "certificate_type": "ResponseSigning",
          "expiry_date": "2025-06-01T00:00:00.000Z",
          "is_expired": false
        },
        "days_until_expiry": 30
      },
      "event": "${EventType.ConnectionSamlCertificateRenewalRequired.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is ConnectionSamlCertificateRenewalRequiredEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionSamlCertificateRenewalRequiredEvent).data.connection.id, connectionId)
    assertEquals(webhook.data.daysUntilExpiry, 30)
  }

  @Test
  fun constructConnectionSamlCertificateRenewedEvent() {
    val workos = createWorkOSClient()
    val webhookData = """
    {
      "id": "$webhookId",
      "data": {
        "connection": {
          "id": "$connectionId",
          "organization_id": "org_01FKPWZWPHE9VN2QXJ7G1BZYP8"
        },
        "certificate": {
          "certificate_type": "ResponseSigning",
          "expiry_date": "2026-06-01T00:00:00.000Z"
        },
        "renewed_at": "2025-05-01T10:00:00.000Z"
      },
      "event": "${EventType.ConnectionSamlCertificateRenewed.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook =
      workos.webhooks.constructEvent(
        webhookData,
        testData["signature"] as String,
        testData["secret"] as String
      )

    assertTrue(webhook is ConnectionSamlCertificateRenewedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as ConnectionSamlCertificateRenewedEvent).data.connection.id, connectionId)
    assertEquals(webhook.data.renewedAt, "2025-05-01T10:00:00.000Z")
  }
}
