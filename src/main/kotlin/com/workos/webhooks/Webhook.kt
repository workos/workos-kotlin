// @oagen-ignore-file
package com.workos.webhooks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.workos.common.crypto.decodeHexOrNull
import com.workos.common.crypto.toHex
import com.workos.common.http.parseSignatureHeader
import com.workos.common.json.ObjectMapperFactory
import com.workos.models.WorkOSEvent
import java.security.MessageDigest
import java.security.SignatureException
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.Throws

/**
 * Helpers for verifying WorkOS webhook payloads.
 *
 * This class is independent of [com.workos.WorkOS] — a webhook handler typically
 * does not have an API key and only needs to validate incoming signatures.
 *
 * @constructor accepts an [ObjectMapper]; the default is the shared SDK mapper.
 */
class Webhook
  @JvmOverloads
  constructor(
    private val objectMapper: ObjectMapper = ObjectMapperFactory.default
  ) {
    /**
     * Validate the `WorkOS-Signature` header against [payload] and return the
     * parsed event body as a [JsonNode]. Callers that want a typed view can
     * deserialize the node to a generated model with [ObjectMapper.treeToValue],
     * or use [constructTypedEvent] to do so in a single call.
     *
     * Use this raw form when you need forward-compatibility with event types
     * the SDK does not yet model (the JSON tree is fully introspectable even
     * for unknown discriminators), or when you want to avoid binding an event
     * payload to a Kotlin class.
     *
     * @param payload raw request body as received from the webhook.
     * @param signatureHeader value of the `WorkOS-Signature` header.
     * @param secret webhook signing secret from the WorkOS Dashboard.
     * @param toleranceMillis maximum age (in ms) a timestamp may have before it
     *   is rejected as a replay. Defaults to 3 minutes.
     */
    @JvmOverloads
    @Throws(SignatureException::class)
    fun constructEvent(
      payload: String,
      signatureHeader: String,
      secret: String,
      toleranceMillis: Long = DEFAULT_TOLERANCE_MILLIS
    ): JsonNode {
      verifyHeader(payload, signatureHeader, secret, toleranceMillis)
      return objectMapper.readTree(payload)
    }

    /**
     * Verify the signature and return a strongly-typed [WorkOSEvent].
     *
     * Use this for ergonomic `when (event) { is UserCreated -> ... }` style
     * pattern matching. Unknown event types deserialize to the
     * [com.workos.models.EventSchema] fallback so the call still succeeds.
     *
     * If you need to inspect raw JSON fields the SDK does not yet model
     * (e.g. fields added on the server before an SDK release), prefer
     * [constructEvent] which returns the raw [JsonNode].
     *
     * @param payload raw request body as received from the webhook.
     * @param signatureHeader value of the `WorkOS-Signature` header.
     * @param secret webhook signing secret from the WorkOS Dashboard.
     * @param toleranceMillis maximum age (in ms) a timestamp may have before it
     *   is rejected as a replay. Defaults to 3 minutes.
     */
    @JvmOverloads
    @Throws(SignatureException::class)
    fun constructTypedEvent(
      payload: String,
      signatureHeader: String,
      secret: String,
      toleranceMillis: Long = DEFAULT_TOLERANCE_MILLIS
    ): WorkOSEvent {
      val node = constructEvent(payload, signatureHeader, secret, toleranceMillis)
      return objectMapper.treeToValue(node)
    }

    /** Throws [SignatureException] if the signature is invalid or expired. */
    @Throws(SignatureException::class)
    fun verifyHeader(
      payload: String,
      signatureHeader: String,
      secret: String,
      toleranceMillis: Long
    ) {
      val (timestamp, signatureHash) = parseSignatureHeader(signatureHeader, setOf("v1", "s"))

      val timestampMs = timestamp.toLongOrNull() ?: throw SignatureException("Timestamp is not a valid long value")
      if (kotlin.math.abs(Instant.now().toEpochMilli() - timestampMs) > toleranceMillis) {
        throw SignatureException("Timestamp outside the tolerance zone")
      }

      val expectedSignature =
        createSignature(timestamp, payload, secret).decodeHexOrNull()
          ?: throw SignatureException("Generated signature was not valid hex")
      val providedSignature =
        signatureHash.decodeHexOrNull()
          ?: throw SignatureException("Signature was not valid hex")
      if (expectedSignature.size != providedSignature.size || !MessageDigest.isEqual(expectedSignature, providedSignature)) {
        throw SignatureException("Signatures do not match")
      }
    }

    /** Compute the expected `HMAC-SHA256(timestamp.payload)` signature. */
    fun createSignature(
      timestamp: String,
      data: String,
      key: String
    ): String {
      val mac = Mac.getInstance("HmacSHA256")
      mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA256"))
      return mac.doFinal("$timestamp.$data".toByteArray()).toHex()
    }

    /** Constants for webhook signature verification. */
    companion object {
      /** 3 minutes, matching the legacy SDK default. */
      const val DEFAULT_TOLERANCE_MILLIS: Long = 180_000L
    }
  }
