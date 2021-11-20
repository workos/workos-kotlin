package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.DirectoryGroupCreatedEvent
import com.workos.webhooks.models.DirectoryGroupDeletedEvent
import com.workos.webhooks.models.DirectoryGroupUpdatedEvent
import com.workos.webhooks.models.EventType
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class DirectoryGroupWebhookTests : TestBase() {

  private val directoryId = "directory_01FM6VV23GH5NKE5TTD2HGTDZ4"
  private val groupId = "directory_group_01FM6VVN382N5NBJ310H2KMGZA"

  private val webhookId = "wh_01FMV8XH3S04J3G8QP9DABQSKX"
  private fun generateGroupWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "id": "$groupId",
        "name": "Account Management",
        "idp_id": "02grqrue4294w24",
        "object": "directory_group",
        "directory_id": "$directoryId",
        "raw_attributes": {
          "id": "02grqrue4294w24",
          "etag": "\"nqbsbhvoIENh0WbZEZYWTG7mnk2phHz4rrCEo-rHT2k/MtDmuYwi32TG-jS3s9jzPA4RCWI\"",
          "kind": "admin#directory#group",
          "name": "Account Management",
          "email": "account-managers@foo-corp.com",
          "description": "",
          "adminCreated": true,
          "directMembersCount": "4",
          "nonEditableAliases": [
            "account-managers@foo-corp.com.test-google-a.com"
          ]
        }
      },
      "event": "${eventType.value}"
    }
    """
  }

  @Test
  fun constructDirectoryGroupCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.DirectoryGroupCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryGroupCreatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryGroupCreatedEvent).data.id, groupId)
  }

  @Test
  fun constructDirectoryGroupDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.DirectoryGroupDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryGroupDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryGroupDeletedEvent).data.id, groupId)
  }

  @Test
  fun constructDirectoryGroupDeletedUpdated() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupWebhookEvent(EventType.DirectoryGroupUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryGroupUpdatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryGroupUpdatedEvent).data.id, groupId)
  }
}
