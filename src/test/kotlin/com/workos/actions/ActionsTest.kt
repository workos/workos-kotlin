// @oagen-ignore-file
package com.workos.actions

import com.workos.WorkOS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.security.SignatureException
import java.time.Instant

class ActionsTest {
  private val actions = Actions()
  private val secret = "action-secret"

  @Test
  fun `constructAction parses a signed payload`() {
    val payload = """{"type":"authentication_action_context","object":"auth_action"}"""
    val timestamp = Instant.now().toEpochMilli().toString()
    val header = "t=$timestamp,v1=${actions.computeSignature(timestamp, payload, secret)}"
    val parsed = actions.constructAction(payload, header, secret)
    assertEquals("authentication_action_context", parsed.path("type").asText())
  }

  @Test
  fun `constructAction rejects tampered payloads`() {
    val payload = """{"x":1}"""
    val timestamp = Instant.now().toEpochMilli().toString()
    val header = "t=$timestamp,v1=${actions.computeSignature(timestamp, payload, secret)}"
    assertThrows(SignatureException::class.java) {
      actions.constructAction("""{"x":2}""", header, secret)
    }
  }

  @Test
  fun `constructAction rejects expired timestamps`() {
    val payload = """{}"""
    val old = (Instant.now().toEpochMilli() - 60_000).toString()
    val header = "t=$old,v1=${actions.computeSignature(old, payload, secret)}"
    assertThrows(SignatureException::class.java) {
      actions.constructAction(payload, header, secret, toleranceMillis = 1_000)
    }
  }

  @Test
  fun `constructAction rejects malformed headers`() {
    assertThrows(SignatureException::class.java) {
      actions.constructAction("{}", "garbage", secret)
    }
    assertThrows(SignatureException::class.java) {
      actions.constructAction("{}", "t=,v1=", secret)
    }
  }

  @Test
  fun `signResponse produces a verifiable payload`() {
    val signed = actions.signResponse(ActionResponseType.AUTHENTICATION, ActionVerdict.ALLOW, secret)
    assertEquals("authentication_action_response", signed.`object`)
    assertEquals("Allow", signed.payload["verdict"])
    assertNotNull(signed.payload["timestamp"])
    assertNotNull(signed.signature)
  }

  @Test
  fun `signResponse includes error_message only on Deny`() {
    val allow = actions.signResponse(ActionResponseType.USER_REGISTRATION, ActionVerdict.ALLOW, secret, errorMessage = "ignored")
    assertTrue(!allow.payload.containsKey("error_message"))
    val deny = actions.signResponse(ActionResponseType.USER_REGISTRATION, ActionVerdict.DENY, secret, errorMessage = "no bots")
    assertEquals("no bots", deny.payload["error_message"])
  }

  @Test
  fun `actions accessor is cached on the WorkOS client`() {
    val workos = WorkOS(apiKey = "sk_test", clientId = "c")
    assertSame(workos.actions, workos.actions)
  }
}
