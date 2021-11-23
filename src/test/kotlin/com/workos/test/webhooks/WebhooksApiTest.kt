package com.workos.test.webhooks

import com.workos.directorysync.models.User
import com.workos.test.TestBase
import org.apache.commons.codec.binary.Hex
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import java.security.SignatureException
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.test.assertEquals

class WebhooksApiTest : TestBase() {
  private val testWebhookId = "wh_123"
  private val eventType = "dsync.user.created"
  private val rawAttributesTitle = "Developer Success Engineer"
  private val directoryUserId = "directory_user_01FAEAJCR3ZBZ30D8BD1924TVG"

  private val testWebhook = """
    {
      "id": "$testWebhookId",
      "data": {
        "id": "$directoryUserId",
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
        "custom_attributes": {},
        "raw_attributes": {
          "name": {
            "givenName": "Blair",
            "familyName": "Lunceford",
            "middleName": "Elizabeth",
            "honorificPrefix": "Ms."
          },
          "title": "$rawAttributesTitle",
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
      "event": "$eventType"
    }"""

  companion object {
    fun prepareTest(webhookData: String): Map<String, Any> {
      val timestamp = Instant.now().toEpochMilli()
      val secret = "secret"
      val sha256Hmac = Mac.getInstance("HmacSHA256")
      val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
      sha256Hmac.init(secretKey)
      val signature = Hex.encodeHexString(
        sha256Hmac.doFinal("$timestamp.$webhookData".toByteArray())
      )

      return mapOf(
        "secret" to secret,
        "signature" to "t=$timestamp, v1=$signature",
        "timestamp" to timestamp,
      )
    }
  }

  @Test
  fun constructEventHappyPath() {
    val workos = createWorkOSClient()
    val testData = prepareTest(testWebhook)

    val webhook = workos.webhooks.constructEvent(
      testWebhook,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertEquals(webhook.id, testWebhookId)
    assertTrue(webhook.data is User)
    assertEquals((webhook.data as User).id, directoryUserId)
  }

  @Test
  fun constructEventThrowsIfGivenIncorrectPayload() {
    assertThrows(SignatureException::class.java) {
      val workos = createWorkOSClient()

      val testData = prepareTest(testWebhook)

      workos.webhooks.constructEvent(

        "wrong payload",
        testData["signature"] as String,
        "secret",
      )
    }
  }

  @Test
  fun constructEventThrowsIfGivenIncorrectSignature() {
    assertThrows(SignatureException::class.java) {
      val workos = createWorkOSClient()
      workos.webhooks.constructEvent(
        testWebhook,
        "wrong signature",
        "secret",
      )
    }
  }

  @Test
  fun constructEventThrowsIfGivenIncorrectSecret() {
    assertThrows(SignatureException::class.java) {
      val workos = createWorkOSClient()

      val testData = prepareTest(testWebhook)

      workos.webhooks.constructEvent(
        testWebhook,
        testData["signature"] as String,
        "not so secret",
      )
    }
  }

  @Test
  fun constructEventThrowsIfToleranceNotMet() {
    assertThrows(SignatureException::class.java) {
      val workos = createWorkOSClient()

      val testData = prepareTest(testWebhook)

      workos.webhooks.constructEvent(
        testWebhook,
        testData["signature"] as String,
        testData["secret"] as String,
        -1
      )
    }
  }
}
