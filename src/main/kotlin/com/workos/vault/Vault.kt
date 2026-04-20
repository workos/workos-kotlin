// @oagen-ignore-file
// The Vault API endpoints are not yet in the OpenAPI spec, so this class is
// hand-maintained. The encrypt/decrypt helpers perform client-side AES-GCM
// cryptography and will always be hand-maintained regardless of spec coverage.
package com.workos.vault

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.workos.WorkOS
import com.workos.common.http.Page
import com.workos.common.http.RequestConfig
import com.workos.common.http.RequestOptions
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/** A key context — arbitrary metadata used to scope a derived key. */
typealias KeyContext = Map<String, String>

/** Plaintext data key material returned by the Vault data-key APIs. */
data class DataKey(
  val id: String,
  val key: String
)

/** A data key pair (plaintext + encrypted form) returned by [Vault.createDataKey]. */
data class DataKeyPair(
  val context: KeyContext,
  val dataKey: DataKey,
  val encryptedKeys: String
)

/** Who last updated a Vault object. */
data class ObjectUpdateBy(
  val id: String,
  val name: String
)

/** Metadata block for a Vault object (no decrypted value). */
data class ObjectMetadata(
  val context: KeyContext,
  @JsonProperty("environment_id") val environmentId: String,
  val id: String,
  @JsonProperty("key_id") val keyId: String,
  @JsonProperty("updated_at") val updatedAt: String,
  @JsonProperty("updated_by") val updatedBy: ObjectUpdateBy,
  @JsonProperty("version_id") val versionId: String
)

/** A full Vault object, with an optional decrypted value. */
data class VaultObject
  @JvmOverloads
  constructor(
    val id: String,
    val metadata: ObjectMetadata,
    val name: String,
    val value: String? = null
  )

/** A summary entry returned by list operations. */
data class ObjectDigest(
  val id: String,
  val name: String,
  @JsonProperty("updated_at") val updatedAt: String
)

/** A single revision entry of a Vault object. */
data class ObjectVersion(
  @JsonProperty("created_at") val createdAt: String,
  @JsonProperty("current_version") val currentVersion: Boolean,
  val id: String
)

/** WorkOS Vault service — encryption, key management, secret storage. */
class Vault(
  private val workos: WorkOS
) {
  private val secureRandom = SecureRandom()
  private val base64 = Base64.getEncoder()
  private val base64Decoder = Base64.getDecoder()

  // -- KV operations --

  @JvmOverloads
  fun listObjects(
    before: String? = null,
    after: String? = null,
    limit: Long? = null,
    requestOptions: RequestOptions? = null
  ): Page<ObjectDigest> {
    fun configFor(afterCursor: String? = null): RequestConfig {
      val params = mutableListOf<Pair<String, String>>()
      if (limit != null) params += "limit" to limit.toString()
      val effectiveAfter = afterCursor ?: after
      if (effectiveAfter == null && before != null) params += "before" to before
      if (effectiveAfter != null) params += "after" to effectiveAfter
      return RequestConfig(
        method = "GET",
        path = "/vault/v1/kv",
        queryParams = params,
        requestOptions = requestOptions
      )
    }
    val itemType = object : TypeReference<ObjectDigest>() {}
    return workos.baseClient.requestPage(configFor(), itemType) { afterCursor -> configFor(afterCursor) }
  }

  @JvmOverloads
  fun createObject(
    name: String,
    value: String,
    keyContext: KeyContext,
    requestOptions: RequestOptions? = null
  ): ObjectMetadata {
    val body = linkedMapOf<String, Any?>()
    body["name"] = name
    body["value"] = value
    body["key_context"] = keyContext
    val config =
      RequestConfig(
        method = "POST",
        path = "/vault/v1/kv",
        body = body,
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, ObjectMetadata::class.java)
  }

  @JvmOverloads
  fun readObject(
    objectId: String,
    requestOptions: RequestOptions? = null
  ): VaultObject {
    val config =
      RequestConfig(
        method = "GET",
        path = "/vault/v1/kv/$objectId",
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, VaultObject::class.java)
  }

  @JvmOverloads
  fun readObjectByName(
    name: String,
    requestOptions: RequestOptions? = null
  ): VaultObject {
    val config =
      RequestConfig(
        method = "GET",
        path = "/vault/v1/kv/name/$name",
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, VaultObject::class.java)
  }

  @JvmOverloads
  fun getObjectMetadata(
    objectId: String,
    requestOptions: RequestOptions? = null
  ): VaultObject {
    val config =
      RequestConfig(
        method = "GET",
        path = "/vault/v1/kv/$objectId/metadata",
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, VaultObject::class.java)
  }

  @JvmOverloads
  fun updateObject(
    objectId: String,
    value: String,
    versionCheck: String? = null,
    requestOptions: RequestOptions? = null
  ): VaultObject {
    val body = linkedMapOf<String, Any?>()
    body["value"] = value
    if (versionCheck != null) body["version_check"] = versionCheck
    val config =
      RequestConfig(
        method = "PUT",
        path = "/vault/v1/kv/$objectId",
        body = body,
        requestOptions = requestOptions
      )
    return workos.baseClient.request(config, VaultObject::class.java)
  }

  @JvmOverloads
  fun deleteObject(
    objectId: String,
    requestOptions: RequestOptions? = null
  ) {
    val config =
      RequestConfig(
        method = "DELETE",
        path = "/vault/v1/kv/$objectId",
        requestOptions = requestOptions
      )
    workos.baseClient.requestVoid(config)
  }

  @JvmOverloads
  fun listObjectVersions(
    objectId: String,
    before: String? = null,
    after: String? = null,
    limit: Long? = null,
    requestOptions: RequestOptions? = null
  ): Page<ObjectVersion> {
    fun configFor(afterCursor: String? = null): RequestConfig {
      val params = mutableListOf<Pair<String, String>>()
      if (limit != null) params += "limit" to limit.toString()
      val effectiveAfter = afterCursor ?: after
      if (effectiveAfter == null && before != null) params += "before" to before
      if (effectiveAfter != null) params += "after" to effectiveAfter
      return RequestConfig(
        method = "GET",
        path = "/vault/v1/kv/$objectId/versions",
        queryParams = params,
        requestOptions = requestOptions
      )
    }
    val itemType = object : TypeReference<ObjectVersion>() {}
    return workos.baseClient.requestPage(configFor(), itemType) { afterCursor -> configFor(afterCursor) }
  }

  // -- Key operations --

  @JvmOverloads
  fun createDataKey(
    keyContext: KeyContext,
    requestOptions: RequestOptions? = null
  ): DataKeyPair {
    val body = linkedMapOf<String, Any?>()
    body["context"] = keyContext
    val config =
      RequestConfig(
        method = "POST",
        path = "/vault/v1/keys/data-key",
        body = body,
        requestOptions = requestOptions
      )
    val response = workos.baseClient.request(config, CreateDataKeyResponse::class.java)
    return DataKeyPair(
      context = response.context,
      dataKey = DataKey(id = response.id, key = response.dataKey),
      encryptedKeys = response.encryptedKeys
    )
  }

  @JvmOverloads
  fun decryptDataKey(
    keys: String,
    requestOptions: RequestOptions? = null
  ): DataKey {
    val body = linkedMapOf<String, Any?>()
    body["keys"] = keys
    val config =
      RequestConfig(
        method = "POST",
        path = "/vault/v1/keys/decrypt",
        body = body,
        requestOptions = requestOptions
      )
    val response = workos.baseClient.request(config, DecryptDataKeyResponse::class.java)
    return DataKey(id = response.id, key = response.dataKey)
  }

  // -- Client-side encryption --

  /**
   * Encrypt [data] locally using AES-GCM with a data key derived from
   * [keyContext]. Returns a base64-encoded payload:
   * `[IV:12][TAG:16][LEB128 keyBlobLen][keyBlob][ciphertext]`.
   */
  @JvmOverloads
  fun encrypt(
    data: String,
    keyContext: KeyContext,
    associatedData: String? = null
  ): String {
    val pair = createDataKey(keyContext)
    val key = base64Decoder.decode(pair.dataKey.key)
    val keyBlob = base64Decoder.decode(pair.encryptedKeys)
    val aad = associatedData?.toByteArray(Charsets.UTF_8)
    val iv = ByteArray(IV_LENGTH_BYTES).also { secureRandom.nextBytes(it) }

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_BITS, iv))
    if (aad != null) cipher.updateAAD(aad)
    val combined = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    // AES/GCM/NoPadding returns ciphertext || tag.
    val ciphertext = combined.copyOfRange(0, combined.size - TAG_LENGTH_BYTES)
    val tag = combined.copyOfRange(combined.size - TAG_LENGTH_BYTES, combined.size)

    val lenPrefix = encodeU32Leb128(keyBlob.size)
    val payload = ByteArray(iv.size + tag.size + lenPrefix.size + keyBlob.size + ciphertext.size)
    var offset = 0
    iv.copyInto(payload, offset)
    offset += iv.size
    tag.copyInto(payload, offset)
    offset += tag.size
    lenPrefix.copyInto(payload, offset)
    offset += lenPrefix.size
    keyBlob.copyInto(payload, offset)
    offset += keyBlob.size
    ciphertext.copyInto(payload, offset)
    return base64.encodeToString(payload)
  }

  /**
   * Decrypt a payload previously produced by [encrypt]. Returns the plaintext
   * UTF-8 string.
   */
  @JvmOverloads
  fun decrypt(
    encryptedData: String,
    associatedData: String? = null
  ): String {
    val decoded = decodeEncryptedPayload(encryptedData)
    val dataKey = decryptDataKey(decoded.keys)
    val key = base64Decoder.decode(dataKey.key)
    val aad = associatedData?.toByteArray(Charsets.UTF_8)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_BITS, decoded.iv))
    if (aad != null) cipher.updateAAD(aad)
    // JCE expects ciphertext || tag as a single input for decryption.
    val input = ByteArray(decoded.ciphertext.size + decoded.tag.size)
    decoded.ciphertext.copyInto(input, 0)
    decoded.tag.copyInto(input, decoded.ciphertext.size)
    val plaintext = cipher.doFinal(input)
    return String(plaintext, Charsets.UTF_8)
  }

  private data class DecodedPayload(
    val iv: ByteArray,
    val tag: ByteArray,
    val keys: String,
    val ciphertext: ByteArray
  )

  private data class CreateDataKeyResponse(
    val context: KeyContext,
    @JsonProperty("data_key") val dataKey: String,
    @JsonProperty("encrypted_keys") val encryptedKeys: String,
    val id: String
  )

  private data class DecryptDataKeyResponse(
    @JsonProperty("data_key") val dataKey: String,
    val id: String
  )

  private fun decodeEncryptedPayload(b64: String): DecodedPayload {
    val raw = base64Decoder.decode(b64)
    require(raw.size >= IV_LENGTH_BYTES + TAG_LENGTH_BYTES + 1) {
      "Encrypted payload is too short"
    }
    val iv = raw.copyOfRange(0, IV_LENGTH_BYTES)
    val tag = raw.copyOfRange(IV_LENGTH_BYTES, IV_LENGTH_BYTES + TAG_LENGTH_BYTES)
    val (keyLen, lebLen) = decodeU32Leb128(raw, IV_LENGTH_BYTES + TAG_LENGTH_BYTES)
    val keysStart = IV_LENGTH_BYTES + TAG_LENGTH_BYTES + lebLen
    val keysEnd = keysStart + keyLen
    val keysSlice = raw.copyOfRange(keysStart, keysEnd)
    val keysBase64 = base64.encodeToString(keysSlice)
    val ciphertext = raw.copyOfRange(keysEnd, raw.size)
    return DecodedPayload(iv = iv, tag = tag, keys = keysBase64, ciphertext = ciphertext)
  }

  private fun encodeU32Leb128(value: Int): ByteArray {
    require(value >= 0) { "LEB128 value must be non-negative" }
    var remaining = value
    val out = ArrayList<Byte>(5)
    while (true) {
      var byte = remaining and 0x7F
      remaining = remaining ushr 7
      if (remaining != 0) byte = byte or 0x80
      out += byte.toByte()
      if (remaining == 0) break
    }
    return out.toByteArray()
  }

  private fun decodeU32Leb128(
    buf: ByteArray,
    offset: Int
  ): Pair<Int, Int> {
    var result = 0
    var shift = 0
    var bytesRead = 0
    var index = offset
    while (index < buf.size) {
      val b = buf[index].toInt() and 0xFF
      bytesRead++
      if (bytesRead > MAX_LEB128_BYTES_U32) {
        throw IllegalArgumentException("LEB128 sequence exceeds maximum length for uint32")
      }
      result = result or ((b and 0x7F) shl shift)
      index++
      if (b and 0x80 == 0) return result to bytesRead
      shift += 7
    }
    throw IllegalArgumentException("Truncated LEB128 encoding")
  }

  private fun ArrayList<Byte>.toByteArray(): ByteArray {
    val out = ByteArray(size)
    for (i in indices) out[i] = this[i]
    return out
  }

  companion object {
    private const val IV_LENGTH_BYTES = 12
    private const val TAG_LENGTH_BYTES = 16
    private const val GCM_TAG_BITS = 128
    private const val MAX_LEB128_BYTES_U32 = 5
  }
}

/** Hand-maintained accessor on the WorkOS client. */
val WorkOS.vault: Vault
  get() = service(Vault::class) { Vault(this) }
