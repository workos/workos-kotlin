package com.workos.webhooks
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.workos.webhooks.models.Webhook
import org.apache.commons.codec.binary.Hex
// import java.lang.Exception
import java.security.MessageDigest
import java.security.SignatureException
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.Throws

class WebhooksApi {
  companion object {
    private val objectMapper = jacksonObjectMapper()

    @JvmStatic
    @Throws(SignatureException::class)
    fun constructEvent(
      payload: String,
      signatureHeader: String,
      secret: String,
      tolerance: Int = 360
    ): Webhook {
      try {
        verifyHeader(payload, signatureHeader, secret, tolerance)
        return objectMapper.readValue(payload, Webhook::class.java)
      } catch (e: Exception) {
        throw SignatureException("Invalid signature: ${e.message}")
      }
    }

    private fun verifyHeader(
      payload: String,
      signatureHeader: String,
      secret: String,
      tolerance: Int
    ) {
      val splitHeader = signatureHeader.split(",")
      val timestamp = splitHeader[0].split("=")[1]
      val signatureHash = splitHeader[1].split("=")[1]

      val toleranceTimestamp = Instant.now().toEpochMilli() - tolerance

      if (timestamp.toLong() < toleranceTimestamp) {
        throw SignatureException("Timestamp outside the tolerance zone")
      }

      val expectedSignature = createSignature(
        timestamp,
        payload,
        secret
      )

      if (!MessageDigest.isEqual(
          expectedSignature.toByteArray(),
          signatureHash.toByteArray()
        )
      ) {
        throw SignatureException("Signatures do not match '$expectedSignature', '$signatureHash'")
      }
    }

    private fun createSignature(timestamp: String, data: String, key: String): String {
      val payload = "$timestamp.$data"
      val sha256Hmac = Mac.getInstance("HmacSHA256")
      val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
      sha256Hmac.init(secretKey)
      return Hex.encodeHexString(sha256Hmac.doFinal(payload.toByteArray()))
    }
  }
}
