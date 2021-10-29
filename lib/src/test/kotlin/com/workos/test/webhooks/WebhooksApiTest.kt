package com.workos.test.webhooks

import com.workos.webhooks.WebhooksApi
import org.apache.commons.codec.binary.Hex
import org.junit.Test
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.test.assertEquals

class WebhooksApiTest {
  val testWebhookId = "wh_123"

  val testWebhook = """
    {
      "id": "$testWebhookId",
      "data": {
        "id": "directory_user_01FAEAJCR3ZBZ30D8BD1924TVG",
        "state": "active",
        "emails": [{
          "type": "work",
          "value": "blair@foo-corp.com",
          "primary": true
        }],
        "idp_id": "00u1e8mutl6wlH3lL4x7",
        "object": "directory_user",
        "username": "blair@foo-corp.com",
        "last_name": "Lunceford",
        "first_name": "Blair",
        "directory_id": "directory_01F9M7F68PZP8QXP8G7X5QRHS7",
        "raw_attributes": {
          "name": {
            "givenName": "Blair",
            "familyName": "Lunceford",
            "middleName": "Elizabeth",
            "honorificPrefix": "Ms."
          },
          "title": "Developer Success Engineer",
          "active": true,
          "emails": [{
            "type": "work",
            "value": "blair@foo-corp.com",
            "primary": true
          }],
          "groups": [],
          "locale": "en-US",
          "schemas": ["urn:ietf:params:scim:schemas:core:2.0:User", "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"],
          "userName": "blair@foo-corp.com",
          "addresses": [{
            "region": "CO",
            "primary": true,
            "locality": "Steamboat Springs",
            "postalCode": "80487"
          }],
          "externalId": "00u1e8mutl6wlH3lL4x7",
          "displayName": "Blair Lunceford",
          "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User": {
            "manager": {
              "value": "2",
              "displayName": "Kathleen Chung"
            },
            "division": "Engineering",
            "department": "Customer Success"
          }
        }
      },
      "event": "dsync.user.created"
    }"""

  private fun prepareTest(): Map<String, Any> {
    val timestamp = Instant.now().toEpochMilli()
    val secret = "secret"
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)
    val signature = Hex.encodeHexString(
      sha256Hmac.doFinal("$timestamp.$testWebhook".toByteArray())
    )

    return mapOf(
      "secret" to secret,
      "signature" to "t=$timestamp, v1=$signature",
      "timestamp" to timestamp,
    )
  }

  @Test
  fun constructEventHappyPath() {
    val testData = prepareTest()

    val webhook = WebhooksApi.constructEvent(
      testWebhook,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertEquals(webhook.id, testWebhookId)
  }
}
