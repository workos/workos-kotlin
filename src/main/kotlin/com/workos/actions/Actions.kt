// @oagen-ignore-file
// Hand-maintained AuthKit Actions helper: verify signed action requests
// and sign action responses. The wire format matches workos-node's
// `Actions` class exactly (HMAC-SHA-256 over `<timestamp>.<JSON payload>`,
// header form `t=<ms>,v1=<hex>`).
package com.workos.actions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.workos.WorkOS
import com.workos.common.crypto.decodeHexOrNull
import com.workos.common.crypto.toHex
import com.workos.common.http.parseSignatureHeader
import com.workos.common.json.ObjectMapperFactory
import java.security.MessageDigest
import java.security.SignatureException
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.Throws

/** Whether an action was approved or denied. */
enum class ActionVerdict(
  /** The wire value sent in the signed response payload. */
  val value: String
) {
  ALLOW("Allow"),
  DENY("Deny")
}

/** Type of action whose response is being signed. */
enum class ActionResponseType(
  /** The `object` discriminator string sent in the signed response. */
  val objectName: String
) {
  AUTHENTICATION("authentication_action_response"),
  USER_REGISTRATION("user_registration_action_response")
}

/** A signed response payload ready to be sent back to AuthKit. */
data class SignedActionResponse(
  /** The object type discriminator (e.g. `"authentication_action_response"`). */
  val `object`: String,
  /** The response payload containing timestamp, verdict, and optional error. */
  val payload: Map<String, Any?>,
  /** The HMAC-SHA-256 hex signature of the payload. */
  val signature: String
)

/**
 * AuthKit Actions helper. Stateless — instantiate once (or reach it via
 * `workos.actions`) and call [constructAction] / [signResponse] per request.
 */
class Actions
  @JvmOverloads
  constructor(
    private val objectMapper: ObjectMapper = ObjectMapperFactory.default
  ) {
    /**
     * Verify the signature header on an incoming AuthKit action request and
     * return the parsed payload. Throws [SignatureException] on any
     * verification failure.
     *
     * @param payload the raw request body (JSON string) as received.
     * @param sigHeader the `WorkOS-Signature` header value.
     * @param secret the shared signing secret from the WorkOS dashboard.
     * @param toleranceMillis max age (ms) a timestamp may have before rejection.
     */
    @JvmOverloads
    @Throws(SignatureException::class)
    fun constructAction(
      payload: String,
      sigHeader: String,
      secret: String,
      toleranceMillis: Long = DEFAULT_TOLERANCE_MILLIS
    ): JsonNode {
      verifyHeader(payload, sigHeader, secret, toleranceMillis)
      return objectMapper.readTree(payload)
    }

    /** Throws [SignatureException] if the signature is invalid or expired. */
    @Throws(SignatureException::class)
    fun verifyHeader(
      payload: String,
      sigHeader: String,
      secret: String,
      toleranceMillis: Long = DEFAULT_TOLERANCE_MILLIS
    ) {
      val (timestamp, signatureHash) = parseSignatureHeader(sigHeader, setOf("v1"))
      val timestampMs =
        timestamp.toLongOrNull()
          ?: throw SignatureException("Timestamp is not a valid long value")
      if (timestampMs < Instant.now().toEpochMilli() - toleranceMillis) {
        throw SignatureException("Timestamp outside the tolerance zone")
      }
      val expected =
        computeSignature(timestamp, payload, secret).decodeHexOrNull()
          ?: throw SignatureException("Generated signature was not valid hex")
      val provided =
        signatureHash.decodeHexOrNull()
          ?: throw SignatureException("Signature hash was not valid hex")
      if (expected.size != provided.size || !MessageDigest.isEqual(expected, provided)) {
        throw SignatureException("Signature hash does not match")
      }
    }

    /**
     * Sign an AuthKit action response. Returns a ready-to-send object with a
     * `timestamp` and `verdict` in `payload`, a `signature` field containing
     * the HMAC, and an `object` discriminator keyed off [type].
     */
    @JvmOverloads
    fun signResponse(
      type: ActionResponseType,
      verdict: ActionVerdict,
      secret: String,
      errorMessage: String? = null
    ): SignedActionResponse {
      val timestamp = Instant.now().toEpochMilli()
      val payload = linkedMapOf<String, Any?>()
      payload["timestamp"] = timestamp
      payload["verdict"] = verdict.value
      if (verdict == ActionVerdict.DENY && !errorMessage.isNullOrEmpty()) {
        payload["error_message"] = errorMessage
      }
      val signature = computeSignature(timestamp.toString(), objectMapper.writeValueAsString(payload), secret)
      return SignedActionResponse(
        `object` = type.objectName,
        payload = payload,
        signature = signature
      )
    }

    /**
     * Low-level primitive: HMAC-SHA-256 of `<timestamp>.<payload>` hex-encoded.
     * Shared with [signResponse] and [verifyHeader].
     */
    fun computeSignature(
      timestamp: String,
      payload: String,
      secret: String
    ): String {
      val mac = Mac.getInstance("HmacSHA256")
      mac.init(SecretKeySpec(secret.toByteArray(), "HmacSHA256"))
      return mac.doFinal("$timestamp.$payload".toByteArray()).toHex()
    }

    /** Constants for the Actions helper. */
    companion object {
      /** 30 seconds, matching workos-node's `constructAction` default. */
      const val DEFAULT_TOLERANCE_MILLIS: Long = 30_000L
    }
  }

/** Hand-maintained accessor on the WorkOS client. */
val WorkOS.actions: Actions
  get() = service(Actions::class) { Actions() }
