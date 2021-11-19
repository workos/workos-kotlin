package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.DirectoryUserCreatedEvent
import org.junit.Test
import kotlin.test.assertEquals

class DirectoryUserWebhookTests : TestBase() {

  private val directoryId = "directory_01FM6VV23GH5NKE5TTD2HGTDZ4"
  private val userId = "directory_user_01FMV8XE3E2CJH7TRBKMSBH8QF"

  private val userCreatedWebhookId = "wh_01FMV8XH0MB0YDJPDT2Q4QHG14"
  private val userCreatedWebhook = """
    {
      "id": "$userCreatedWebhookId",
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
      "event": "dsync.user.created"
    }
  """

  @Test
  fun constructUserCreatedEvent() {
    val workos = createWorkOSClient()
    val testData = WebhooksApiTest.prepareTest(userCreatedWebhook)

    val webhook = workos.webhooks.constructEvent(
      userCreatedWebhook,
      testData["signature"] as String,
      testData["secret"] as String
    ) as DirectoryUserCreatedEvent

    assertEquals(webhook.id, userCreatedWebhookId)
    assertEquals(webhook.data.id, userId)
    assertEquals(webhook.data.directoryId, directoryId)
  }
}
