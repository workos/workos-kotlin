// @oagen-ignore-file
package com.workos.webhooks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.workos.common.json.ObjectMapperFactory
import org.apache.commons.codec.binary.Hex
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
    private val objectMapper: ObjectMapper = ObjectMapperFactory.create()
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
      val parts = signatureHeader.split(",")
      if (parts.size < 2) throw SignatureException("Malformed WorkOS-Signature header")
      val timestamp = parts[0].substringAfter("t=", missingDelimiterValue = "")
      val signatureHash =
        parts[1].substringAfter("v1=", missingDelimiterValue = "").ifEmpty {
          parts[1].substringAfter("s=", missingDelimiterValue = "")
        }
      if (timestamp.isEmpty() || signatureHash.isEmpty()) {
        throw SignatureException("Missing timestamp or signature component in WorkOS-Signature header")
      }

      val timestampMs = timestamp.toLongOrNull() ?: throw SignatureException("Timestamp is not a valid long value")
      val oldestAcceptable = Instant.now().toEpochMilli() - toleranceMillis
      if (timestampMs < oldestAcceptable) {
        throw SignatureException("Timestamp outside the tolerance zone")
      }

      val expectedSignature = createSignature(timestamp, payload, secret)
      if (!MessageDigest.isEqual(expectedSignature.toByteArray(), signatureHash.toByteArray())) {
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
      return Hex.encodeHexString(mac.doFinal("$timestamp.$data".toByteArray()))
    }

    companion object {
      /** 3 minutes, matching the legacy SDK default. */
      const val DEFAULT_TOLERANCE_MILLIS: Long = 180_000L
    }
  }
