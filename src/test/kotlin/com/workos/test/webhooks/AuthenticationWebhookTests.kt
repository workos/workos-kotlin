package com.workos.test.webhooks

import com.workos.test.TestBase
import com.workos.webhooks.models.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class AuthenticationWebhookTests : TestBase() {

  private val userId = "user_01FMXJ0YAP7JX3377YFV2XPCJE"
  private val email = "mary@example.com"

  private val webhookId = "wh_01FMXKE185HQ2DQ84BH33HMF99"

  private fun generateAuthenticationSucceededWebhookEvent(eventType: EventType, dataType: String): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "type": "$dataType",
        "status": "succeeded",
        "user_id": "$userId",
        "email": "$email",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36"
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  private fun generateAuthenticationFailedWebhookEvent(eventType: EventType, dataType: String): String {
    return """
    {
      "id": "$webhookId",
      "data": {
        "type": "$dataType",
        "status": "failed",
        "user_id": "$userId",
        "email": "$email",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36",
        "error": {
          "code": "error_code",
          "message": "Error message."
        }
      },
      "event": "${eventType.value}",
      "created_at": "2024-07-20T10:15:23.713Z"
    }
    """
  }

  @Test
  fun constructAuthenticationEmailVerificationSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationSucceededWebhookEvent(EventType.AuthenticationEmailVerificationSucceeded, "email_verification")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationMagicAuthFailedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationFailedWebhookEvent(EventType.AuthenticationMagicAuthFailed, "magic_auth")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationMagicAuthSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationSucceededWebhookEvent(EventType.AuthenticationMagicAuthSucceeded, "magic_auth")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationMfaSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationSucceededWebhookEvent(EventType.AuthenticationMfaSucceeded, "mfa")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationOauthFailedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationFailedWebhookEvent(EventType.AuthenticationOauthFailed, "oauth")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationOauthSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationSucceededWebhookEvent(EventType.AuthenticationOauthSucceeded, "oauth")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationPasswordFailedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationFailedWebhookEvent(EventType.AuthenticationPasswordFailed, "password")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationPasswordSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationSucceededWebhookEvent(EventType.AuthenticationPasswordSucceeded, "password")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationSsoFailedEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationFailedWebhookEvent(EventType.AuthenticationSsoFailed, "sso")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }

  @Test
  fun constructAuthenticationSsoSucceededEvent() {
    val workos = createWorkOSClient()
    val webhookData = generateAuthenticationSucceededWebhookEvent(EventType.AuthenticationSsoSucceeded, "sso")
    val testData = WebhooksApiTest.prepareTest(webhookData)

    val webhook = workos.webhooks.constructEvent(
      webhookData,
      testData["signature"] as String,
      testData["secret"] as String
    )

    assertTrue(webhook is AuthenticationEvent)
    assertEquals(webhook.id, webhookId)
    assertEquals((webhook as AuthenticationEvent).data.userId, userId)
    assertEquals((webhook as AuthenticationEvent).data.email, email)
  }
}
