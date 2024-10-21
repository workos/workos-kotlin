package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.DirectoryUserCreatedEvent
import com.workos.webhooks.models.DirectoryUserDeletedEvent
import com.workos.webhooks.models.DirectoryUserUpdatedEvent
import com.workos.webhooks.models.EventType
import org.junit.Test
import org.junit.jupiter.api.Assertions
import kotlin.test.assertEquals

class DirectoryUserWebhookTests : TestBase() {

  private val directoryId = "directory_01FM6VV23GH5NKE5TTD2HGTDZ4"
  private val userId = "directory_user_01FMV8XE3E2CJH7TRBKMSBH8QF"

  private val webhookId = "wh_01FMV8XH0MB0YDJPDT2Q4QHG14"
  private fun generateUserWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
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
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:08:33.155Z",
        "directory_id": "$directoryId",
        "role": { "slug": "member" },
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
      "event": "${eventType.value}",
      "created_at": "2021-06-25T19:07:33.155Z"
    }"""
  }

  private fun generateUserUpdatedWebhookEvent(eventType: EventType): String {
    return """
    {
      "id": "$webhookId",
      "data": {
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
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:08:33.155Z",
        "directory_id": "$directoryId",
        "role": { "slug": "admin" },
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
        "custom_attributes": {},
        "previous_attributes": {
        "role": { "slug": "member" },
          "raw_attributes": {
            "emails": [
              {
                "address": "michael.hadley@foo-corp.com",
                "primary": true
              },
              {
                "address": "michael.hadley@foo-corp.com.test-google-a.com"
              }
            ],
            "aliases": null,
            "nonEditableAliases": [
              "michael.hadley@foo-corp.com.test-google-a.com"
            ]
          }
        }
      },
      "event": "${eventType.value}",
      "created_at": "2021-06-25T19:07:33.155Z"
    }"""
  }

  @Test
  fun constructDirectoryUserCreatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateUserWebhookEvent(EventType.DirectoryUserCreated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    Assertions.assertTrue(webhook is DirectoryUserCreatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryUserCreatedEvent).data.id, userId)
  }

  @Test
  fun constructDirectoryUserDeletedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateUserWebhookEvent(EventType.DirectoryUserDeleted)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    Assertions.assertTrue(webhook is DirectoryUserDeletedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryUserDeletedEvent).data.id, userId)
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun constructDirectoryUserUpdatedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateUserUpdatedWebhookEvent(EventType.DirectoryUserUpdated)
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    Assertions.assertTrue(webhook is DirectoryUserUpdatedEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as DirectoryUserUpdatedEvent).data.id, userId)
    val previousRawAttributes = webhook.data.previousAttributes["raw_attributes"] as Map<String, Any?>
    assertEquals(previousRawAttributes["aliases"], null)
    assertEquals((previousRawAttributes["emails"] as List<Any>).size, 2)
  }
}
