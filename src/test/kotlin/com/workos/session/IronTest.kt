// @oagen-ignore-file
package com.workos.session

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class IronTest {
  private val password = "this-is-at-least-thirty-two-chars!"

  @Test
  fun `seal-then-unseal round-trips plaintext`() {
    val sealed = Iron.seal("""{"user":"alice"}""", password)
    assertEquals("""{"user":"alice"}""", Iron.unseal(sealed, password))
  }

  @Test
  fun `two seals of the same data produce different outputs`() {
    val a = Iron.seal("hello", password)
    val b = Iron.seal("hello", password)
    assertNotEquals(a, b)
  }

  @Test
  fun `seal format begins with Fe26-2 and has eight star-separated parts`() {
    val sealed = Iron.seal("x", password)
    assert(sealed.startsWith("Fe26.2*1*"))
    assertEquals(8, sealed.split("*").size)
  }

  @Test
  fun `unseal rejects a seal whose hmac was tampered with`() {
    val sealed = Iron.seal("payload", password)
    val parts = sealed.split("*").toMutableList()
    // Replace the first char of the hmac with one that's guaranteed different
    // (base64url leading char always carries 6 bits of data, unlike the
    // trailing char which encodes only 4 meaningful bits for a 32-byte hmac).
    val head = parts[7]
    val replacement = if (head.first() == 'A') 'B' else 'A'
    parts[7] = replacement + head.drop(1)
    val bad = parts.joinToString("*")
    val ex = assertThrows(IronException::class.java) { Iron.unseal(bad, password) }
    assert(ex.message!!.contains("Bad hmac"))
  }

  @Test
  fun `unseal rejects the wrong password`() {
    val sealed = Iron.seal("payload", password)
    val otherPassword = "a-different-password-at-least-32chars"
    assertThrows(IronException::class.java) { Iron.unseal(sealed, otherPassword) }
  }

  @Test
  fun `unseal rejects a malformed seal`() {
    assertThrows(IronException::class.java) { Iron.unseal("not-a-seal", password) }
  }

  @Test
  fun `seal rejects short passwords`() {
    assertThrows(IllegalArgumentException::class.java) { Iron.seal("x", "too-short") }
  }

  @Test
  fun `expired seals are rejected`() {
    val sealed = Iron.seal("payload", password, ttlMillis = 1)
    Thread.sleep(10)
    val ex = assertThrows(IronException::class.java) { Iron.unseal(sealed, password) }
    assert(ex.message!!.contains("Expired"))
  }

  @Test
  fun `unseal accepts a fixed iron-webcrypto fixture from workos-node`() {
    val sealed =
      "Fe26.2*1*570ef110e7801942ced24e79800baeb86d4f803120fa48b1c057bfb083ee16d6*M5mIO8mKHP8wFo5VxgIC_g*cKhTnmvcPkxH6euF9Bn5TvhrlOvOwQMb92PbnaVMiOs**248b6fba3acd1ab0d31c5a2698a057dd0df62e319dce60fae2d4ddd8420e25f6*pV7Ipp4YGGBlIdhYCEidU4coZiSLrgyb4qlWXCxDcqk"
    assertEquals("""{"user":"alice"}""", Iron.unseal(sealed, password))
  }
}
