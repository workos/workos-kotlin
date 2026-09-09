// @oagen-ignore-file
package com.workos.session

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

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
  fun `unseal rejects malformed base64 hmac with IronException`() {
    for (hmac in listOf("!!", "A", "ab=c")) {
      val ex = assertThrows(IronException::class.java) { Iron.unseal("Fe26.2*1*aa*bb*cc**dd*$hmac", password) }
      assertEquals("Invalid seal encoding", ex.message)
    }
  }

  @Test
  fun `unseal rejects authenticated malformed base64 iv and ciphertext with IronException`() {
    for (field in listOf(3, 4)) {
      for (encoding in listOf("!!", "A", "ab=c")) {
        val parts = Iron.seal("payload", password).split("*").toMutableList()
        parts[field] = encoding
        val ex = assertThrows(IronException::class.java) { Iron.unseal(resign(parts), password) }
        assertEquals("Invalid seal encoding", ex.message)
      }
    }
  }

  @Test
  fun `unseal rejects an empty integrity salt with IronException`() {
    val parts = Iron.seal("payload", password).split("*").toMutableList()
    parts[6] = ""
    val ex = assertThrows(IronException::class.java) { Iron.unseal(parts.joinToString("*"), password) }
    assertEquals("Invalid seal encoding", ex.message)
  }

  @Test
  fun `unseal rejects an authenticated empty encryption salt with IronException`() {
    val parts = Iron.seal("payload", password).split("*").toMutableList()
    parts[2] = ""
    val ex = assertThrows(IronException::class.java) { Iron.unseal(resign(parts), password) }
    assertEquals("Invalid seal encoding", ex.message)
  }

  @Test
  fun `unseal still rejects short passwords with IllegalArgumentException`() {
    val ex =
      assertThrows(IllegalArgumentException::class.java) {
        Iron.unseal("Fe26.2*1*aa*bb*cc**dd*!!", "too-short")
      }
    assertEquals("Password must be at least 32 characters", ex.message)
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

  // Recompute the HMAC so malformed encrypted fields reach their decoding paths.
  private fun resign(parts: MutableList<String>): String {
    val spec = PBEKeySpec(password.toCharArray(), parts[6].toByteArray(Charsets.UTF_8), 1, 256)
    val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    val hmac = mac.doFinal(parts.take(6).joinToString("*").toByteArray(Charsets.UTF_8))
    parts[7] = Base64.getUrlEncoder().withoutPadding().encodeToString(hmac)
    return parts.joinToString("*")
  }

  @Test
  fun `unseal accepts a fixed iron-webcrypto fixture from workos-node`() {
    val sealed =
      "Fe26.2*1*570ef110e7801942ced24e79800baeb86d4f803120fa48b1c057bfb083ee16d6*M5mIO8mKHP8wFo5VxgIC_g*cKhTnmvcPkxH6euF9Bn5TvhrlOvOwQMb92PbnaVMiOs**248b6fba3acd1ab0d31c5a2698a057dd0df62e319dce60fae2d4ddd8420e25f6*pV7Ipp4YGGBlIdhYCEidU4coZiSLrgyb4qlWXCxDcqk"
    assertEquals("""{"user":"alice"}""", Iron.unseal(sealed, password))
  }
}
