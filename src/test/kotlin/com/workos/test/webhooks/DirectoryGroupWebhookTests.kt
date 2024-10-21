package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class DirectoryGroupWebhookTests : TestBase() {

  private val directoryId = "directory_01FM6VV23GH5NKE5TTD2HGTDZ4"
  private val groupId = "directory_group_01FM6VVN382N5NBJ310H2KMGZA"
  private val userId = "directory_user_01FMV8XE3E2CJH7TRBKMSBH8QF"

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
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:08:33.155Z",
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
      "event": "${eventType.value}",
      "created_at": "2021-06-25T19:07:33.155Z"
    }
    """
  }

  private fun generateGroupUserWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "user": {
          "id": "$userId",
          "state": "active",
          "emails": [
            {
              "value": "michael.hadley@foo-corp.com",
              "primary": true
            }
          ],
          "idp_id": "105777651754615852813",
          "object": "directory_user",
          "username": "michael.hadley@foo-corp.com",
          "last_name": "Hadley",
          "first_name": "Michael",
          "job_title": "Software Engineer",
          "directory_id": "$directoryId",
          "role": { "slug": "member" },
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z",
          "raw_attributes": {
            "id": "105777651754615852813",
            "etag": "\"nqbsbhvoIENh0WbZEZYWTG7mnk2phHz4rrCEo-rHT2k/E5jQHrdS88NS4ACUhZ4m9CYVR30\"",
            "kind": "admin#directory#user",
            "name": {
              "fullName": "Michael Hadley",
              "givenName": "Michael",
              "familyName": "Hadley"
            },
            "emails": [
              {
                "address": "michael.hadley@foo-corp.com",
                "primary": true
              },
              {
                "address": "michael.hadley@foo-corp.com.test-google-a.com"
              }
            ],
            "languages": [
              {
                "languageCode": "en"
              }
            ],
            "customerId": "C02mw6qrc",
            "orgUnitPath": "/",
            "creationTime": "2021-11-19T04:50:39.000Z",
            "primaryEmail": "michael.hadley@foo-corp.com",
            "agreedToTerms": true,
            "lastLoginTime": "2021-11-19T04:53:26.000Z",
            "isMailboxSetup": true,
            "isDelegatedAdmin": true,
            "nonEditableAliases": [
              "michael.hadley@foo-corp.com.test-google-a.com"
            ],
            "includeInGlobalAddressList": true
          },
          "custom_attributes": {}
        },
        "group": {
          "id": "$groupId",
          "name": "Account Management",
          "idp_id": "02grqrue4294w24",
          "object": "directory_group",
          "directory_id": "$directoryId",
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:08:33.155Z",
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
        "directory_id": "$directoryId"
      },
      "event": "${eventType.value}",
      "created_at": "2021-06-25T19:07:33.155Z"
    }
    """
  }

  private fun generateGroupUpdatedWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "id": "$groupId",
        "name": "Account Management",
        "idp_id": "02grqrue4294w24",
        "object": "directory_group",
        "directory_id": "$directoryId",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:08:33.155Z",
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
        },
        "previous_attributes": {
          "name": "Account Mgmt",
          "raw_attributes": {
            "name": "Account Mgmt",
            "adminCreated": null
          }
        }
      },
      "event": "${eventType.value}",
      "created_at": "2021-06-25T19:07:33.155Z"
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
  @Suppress("UNCHECKED_CAST")
  fun constructDirectoryGroupUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupUpdatedWebhookEvent(EventType.DirectoryGroupUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryGroupUpdatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryGroupUpdatedEvent).data.id, groupId)
    val previousRawAttributes = webhook.data.previousAttributes["raw_attributes"] as Map<String, Any?>
    assertEquals(previousRawAttributes["adminCreated"], null)
    assertEquals(previousRawAttributes["name"], "Account Mgmt")
  }

  @Test
  fun constructDirectoryGroupUserAddedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupUserWebhookEvent(EventType.DirectoryGroupUserAdded)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryGroupUserAddedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryGroupUserAddedEvent).data.directoryId, directoryId)
    assertEquals(webhook.data.group.id, groupId)
    assertEquals(webhook.data.user.id, userId)
  }

  @Test
  fun constructDirectoryGroupUserRemovedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateGroupUserWebhookEvent(EventType.DirectoryGroupUserRemoved)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is DirectoryGroupUserRemovedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryGroupUserRemovedEvent).data.directoryId, directoryId)
    assertEquals(webhook.data.group.id, groupId)
    assertEquals(webhook.data.user.id, userId)
  }
}
