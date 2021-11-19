package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.DirectoryGroupUser
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class DirectoryGroupUserWebhookTests : TestBase() {

  private val directoryId = "directory_01FM6VV23GH5NKE5TTD2HGTDZ4"
  private val userId = "directory_user_01FMV8XE3E2CJH7TRBKMSBH8QF"
  private val groupId = "directory_group_01FM6VVN382N5NBJ310H2KMGZA"

  private val userRemovedWebhookId = "wh_01FMV8XH3S04J3G8QP9DABQSKX"
  private val groupUserRemovedWebhook = """
    {
      "id": "$userRemovedWebhookId",
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
          "directory_id": "$directoryId",
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
      "event": "dsync.group.user_added"
    }
  """

  @Test
  fun constructEventHappyPath() {
    val workos = createWorkOSClient()
    val testData = WebhooksApiTest.prepareTest(groupUserRemovedWebhook)

    val webhook = workos.webhooks.constructEvent(
      groupUserRemovedWebhook,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertEquals(webhook.id, userRemovedWebhookId)
    assertTrue(webhook.data is DirectoryGroupUser)

    val webhookData = webhook.data as DirectoryGroupUser
    assertEquals(webhookData.directoryId, directoryId)
    assertEquals(webhookData.user.id, userId)
    assertEquals(webhookData.group.id, groupId)
  }
}
