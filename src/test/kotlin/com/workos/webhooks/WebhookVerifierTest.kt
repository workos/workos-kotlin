// @oagen-ignore-file
package com.workos.webhooks

import com.workos.models.UserCreated
import com.workos.models.WorkOSEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.security.SignatureException
import java.time.Instant

class WebhookVerifierTest {
  private val webhooks = Webhook()
  private val secret = "test_secret"
  private val payload =
    """{"id":"event_01","event":"dsync.user.created","data":{"id":"user_1"}}"""

  @Test
  fun `valid signature passes and returns parsed JSON`() {
    val timestamp = Instant.now().toEpochMilli().toString()
    val signature = webhooks.createSignature(timestamp, payload, secret)
    val header = "t=$timestamp, v1=$signature"
    val event = webhooks.constructEvent(payload, header, secret)
    assertEquals("event_01", event.path("id").asText())
    assertEquals("dsync.user.created", event.path("event").asText())
  }

  @Test
  fun `accepts legacy s= signature format`() {
    val timestamp = Instant.now().toEpochMilli().toString()
    val signature = webhooks.createSignature(timestamp, payload, secret)
    val header = "t=$timestamp, s=$signature"
    webhooks.verifyHeader(payload, header, secret, 60_000)
  }

  @Test
  fun `expired timestamp is rejected`() {
    val oldTs = (Instant.now().toEpochMilli() - 10 * 60_000).toString()
    val signature = webhooks.createSignature(oldTs, payload, secret)
    val header = "t=$oldTs, v1=$signature"
    assertThrows(SignatureException::class.java) {
      webhooks.verifyHeader(payload, header, secret, 60_000)
    }
  }

  @Test
  fun `mismatched signature is rejected`() {
    val timestamp = Instant.now().toEpochMilli().toString()
    val header = "t=$timestamp, v1=0000000000000000000000000000000000000000000000000000000000000000"
    assertThrows(SignatureException::class.java) {
      webhooks.verifyHeader(payload, header, secret, 60_000)
    }
  }

  @Test
  fun `malformed header is rejected`() {
    assertThrows(SignatureException::class.java) {
      webhooks.verifyHeader(payload, "bogus", secret, 60_000)
    }
  }

  @Test
  fun `reordered header fields are accepted`() {
    val timestamp = Instant.now().toEpochMilli().toString()
    val signature = webhooks.createSignature(timestamp, payload, secret)
    val header = "v1=$signature, t=$timestamp"
    webhooks.verifyHeader(payload, header, secret, 60_000)
  }

  @Test
  fun `wrong secret is rejected`() {
    val timestamp = Instant.now().toEpochMilli().toString()
    val signature = webhooks.createSignature(timestamp, payload, secret)
    val header = "t=$timestamp, v1=$signature"
    assertThrows(SignatureException::class.java) {
      webhooks.verifyHeader(payload, header, "wrong_secret", 60_000)
    }
  }

  @Test
  fun `tampered payload is rejected`() {
    val timestamp = Instant.now().toEpochMilli().toString()
    val signature = webhooks.createSignature(timestamp, payload, secret)
    val header = "t=$timestamp, v1=$signature"
    assertThrows(SignatureException::class.java) {
      webhooks.verifyHeader("""{"tampered":true}""", header, secret, 60_000)
    }
  }

  @Test
  fun `constructTypedEvent returns typed WorkOSEvent for user created`() {
    val userCreatedPayload =
      """
      {
        "id": "event_01",
        "event": "user.created",
        "created_at": "2024-01-01T00:00:00Z",
        "data": {
          "object": "user",
          "id": "user_1",
          "email": "u@e.com",
          "first_name": null,
          "last_name": null,
          "email_verified": true,
          "profile_picture_url": null,
          "external_id": null,
          "last_sign_in_at": null,
          "created_at": "2024-01-01T00:00:00Z",
          "updated_at": "2024-01-01T00:00:00Z",
          "metadata": {}
        }
      }
      """.trimIndent()
    val timestamp = Instant.now().toEpochMilli().toString()
    val signature = webhooks.createSignature(timestamp, userCreatedPayload, secret)
    val header = "t=$timestamp, v1=$signature"

    val event: WorkOSEvent = webhooks.constructTypedEvent(userCreatedPayload, header, secret)
    assertTrue(event is UserCreated)
    val userCreated = event as UserCreated
    assertInstanceOf(UserCreated::class.java, event)
    assertEquals("event_01", userCreated.id)
    assertEquals("user.created", userCreated.event)
    assertEquals("user_1", userCreated.data.id)
  }
}
