// @oagen-ignore-file
package com.workos.pkce

import com.workos.WorkOS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.security.MessageDigest
import java.util.Base64

class PKCETest {
  private val pkce = PKCE()

  @Test
  fun `generateCodeVerifier returns a 43-character verifier by default`() {
    val verifier = pkce.generateCodeVerifier()
    assertEquals(43, verifier.length)
    assertTrue(verifier.all { it.isBase64UrlChar() })
  }

  @Test
  fun `generateCodeVerifier honors custom lengths in the valid range`() {
    assertEquals(64, pkce.generateCodeVerifier(64).length)
    assertEquals(128, pkce.generateCodeVerifier(128).length)
  }

  @Test
  fun `generateCodeVerifier rejects lengths outside 43-128`() {
    assertThrows(IllegalArgumentException::class.java) { pkce.generateCodeVerifier(42) }
    assertThrows(IllegalArgumentException::class.java) { pkce.generateCodeVerifier(129) }
  }

  @Test
  fun `generateCodeVerifier returns random values`() {
    val a = pkce.generateCodeVerifier()
    val b = pkce.generateCodeVerifier()
    assertNotEquals(a, b)
  }

  @Test
  fun `generateCodeChallenge computes base64url of SHA-256 digest`() {
    val verifier = "test-verifier"
    val expected =
      Base64
        .getUrlEncoder()
        .withoutPadding()
        .encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray()))
    assertEquals(expected, pkce.generateCodeChallenge(verifier))
  }

  @Test
  fun `generate produces a consistent verifier-challenge pair`() {
    val pair = pkce.generate()
    assertEquals("S256", pair.codeChallengeMethod)
    assertEquals(pkce.generateCodeChallenge(pair.codeVerifier), pair.codeChallenge)
  }

  @Test
  fun `pkce accessor is cached on the WorkOS client`() {
    val workos = WorkOS(apiKey = "sk_test", clientId = "client_1")
    assertSame(workos.pkce, workos.pkce)
  }

  private fun Char.isBase64UrlChar(): Boolean = this in '0'..'9' || this in 'A'..'Z' || this in 'a'..'z' || this == '-' || this == '_'
}
