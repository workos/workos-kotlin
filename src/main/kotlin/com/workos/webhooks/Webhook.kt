// @oagen-ignore-file
package com.workos.webhooks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
     * deserialize the node to a generated model with [ObjectMapper.treeToValue].
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
      val oldestAcceptable = Instant.now().toEpochMilli() - toleranceMillis
      if (timestampMs < oldestAcceptable) {
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

    companion object {
      /** 3 minutes, matching the legacy SDK default. */
      const val DEFAULT_TOLERANCE_MILLIS: Long = 180_000L
    }
  }
