// @oagen-ignore-file
package com.workos.pkce

import com.workos.WorkOS
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/** A PKCE (RFC 7636) code verifier and challenge pair. */
data class PKCEPair(
  /** The plain-text code verifier (43-128 characters, RFC 7636 S4.1). */
  val codeVerifier: String,
  /** Base64url-encoded SHA-256 hash of the code verifier. */
  val codeChallenge: String,
  /** Challenge derivation method (always `"S256"`). */
  val codeChallengeMethod: String = "S256"
)

/**
 * PKCE (Proof Key for Code Exchange, RFC 7636) utilities for OAuth 2.0
 * public-client flows. All operations are stateless.
 *
 * Obtain via `workos.pkce` or construct directly; the class holds no state.
 */
class PKCE {
  private val secureRandom = SecureRandom()
  private val base64Url = Base64.getUrlEncoder().withoutPadding()

  /**
   * Generate a cryptographically random code verifier.
   *
   * @param length verifier length between 43 and 128 characters (RFC 7636 §4.1).
   */
  @JvmOverloads
  fun generateCodeVerifier(length: Int = 43): String {
    require(length in 43..128) {
      "Code verifier length must be between 43 and 128, got $length"
    }
    val numBytes = (length * 3 + 3) / 4
    val raw = ByteArray(numBytes)
    secureRandom.nextBytes(raw)
    val encoded = base64Url.encodeToString(raw)
    return encoded.substring(0, length)
  }

  /** Compute the S256 code challenge (base64url-encoded SHA-256) for a verifier. */
  fun generateCodeChallenge(verifier: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
    return base64Url.encodeToString(digest)
  }

  /** Generate a complete PKCE pair (verifier + S256 challenge). */
  fun generate(): PKCEPair {
    val verifier = generateCodeVerifier()
    val challenge = generateCodeChallenge(verifier)
    return PKCEPair(
      codeVerifier = verifier,
      codeChallenge = challenge,
      codeChallengeMethod = "S256"
    )
  }
}

/** Hand-maintained accessor. Survives regeneration via `@oagen-ignore-file`. */
val WorkOS.pkce: PKCE
  get() = service(PKCE::class) { PKCE() }
