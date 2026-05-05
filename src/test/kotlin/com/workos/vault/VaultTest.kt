// @oagen-ignore-file
package com.workos.vault

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.workos.common.exceptions.UnauthorizedException
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val METADATA_JSON = """
  {
    "context": {"env": "test"},
    "environment_id": "env_1",
    "id": "vo_1",
    "key_id": "k_1",
    "updated_at": "2024-01-01T00:00:00Z",
    "updated_by": {"id": "u_1", "name": "test"},
    "version_id": "v_1"
  }
"""

private const val OBJECT_JSON = """
  {
    "id": "vo_1",
    "metadata": $METADATA_JSON,
    "name": "my-secret",
    "value": "plaintext"
  }
"""

class VaultTest : TestBase() {
  private fun api() = Vault(createWorkOSClient())

  @Test
  fun `listObjects returns a Page of ObjectDigest`() {
    wireMockRule.stubFor(
      get(urlPathEqualTo("/vault/v1/kv"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"data":[{"id":"vo_1","name":"a","updated_at":"2024-01-01T00:00:00Z"}],"list_metadata":{"after":null,"before":null}}"""
            )
        )
    )
    val result = api().listObjects()
    assertEquals(1, result.data.size)
    assertEquals("vo_1", result.data[0].id)
  }

  @Test
  fun `createObject posts the payload and returns metadata`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/vault/v1/kv"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(METADATA_JSON)
        )
    )
    val result = api().createObject("s", "v", mapOf("env" to "test"))
    assertEquals("vo_1", result.id)
    assertEquals("env_1", result.environmentId)
  }

  @Test
  fun `readObject returns the object with value`() {
    wireMockRule.stubFor(
      get(urlPathMatching("/vault/v1/kv/[^/]+"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(OBJECT_JSON)
        )
    )
    val result = api().readObject("vo_1")
    assertEquals("plaintext", result.value)
  }

  @Test
  fun `readObjectByName hits the -name- path`() {
    wireMockRule.stubFor(
      get(urlPathEqualTo("/vault/v1/kv/name/my-secret"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(OBJECT_JSON)
        )
    )
    val result = api().readObjectByName("my-secret")
    assertEquals("my-secret", result.name)
  }

  @Test
  fun `getObjectMetadata hits the metadata path`() {
    wireMockRule.stubFor(
      get(urlPathEqualTo("/vault/v1/kv/vo_1/metadata"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(OBJECT_JSON)
        )
    )
    val result = api().getObjectMetadata("vo_1")
    assertNotNull(result)
  }

  @Test
  fun `updateObject issues a PUT`() {
    wireMockRule.stubFor(
      put(urlPathEqualTo("/vault/v1/kv/vo_1"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(OBJECT_JSON)
        )
    )
    val result = api().updateObject("vo_1", "new-value")
    assertEquals("vo_1", result.id)
  }

  @Test
  fun `deleteObject issues a DELETE`() {
    wireMockRule.stubFor(
      delete(urlPathEqualTo("/vault/v1/kv/vo_1"))
        .willReturn(aResponse().withStatus(204))
    )
    api().deleteObject("vo_1")
  }

  @Test
  fun `listObjectVersions unwraps the data array`() {
    wireMockRule.stubFor(
      get(urlPathEqualTo("/vault/v1/kv/vo_1/versions"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"data":[{"id":"v_1","created_at":"2024-01-01T00:00:00Z","current_version":true}],"list_metadata":{"after":null,"before":null}}"""
            )
        )
    )
    val result = api().listObjectVersions("vo_1")
    assertEquals(1, result.data.size)
    assertEquals(true, result.data[0].currentVersion)
  }

  @Test
  fun `createDataKey maps the response into a DataKeyPair`() {
    val key = randomAesKeyBase64()
    wireMockRule.stubFor(
      post(urlPathEqualTo("/vault/v1/keys/data-key"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"context":{"env":"t"},"data_key":"$key","encrypted_keys":"ZW5jcnlwdGVk","id":"dk_1"}"""
            )
        )
    )
    val pair = api().createDataKey(mapOf("env" to "t"))
    assertEquals("dk_1", pair.dataKey.id)
    assertEquals(key, pair.dataKey.key)
  }

  @Test
  fun `decryptDataKey returns a plain DataKey`() {
    val key = randomAesKeyBase64()
    wireMockRule.stubFor(
      post(urlPathEqualTo("/vault/v1/keys/decrypt"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"data_key":"$key","id":"dk_1"}""")
        )
    )
    val result = api().decryptDataKey("ZW5jcnlwdGVk")
    assertEquals("dk_1", result.id)
  }

  @Test
  fun `encrypt then decrypt round-trips the plaintext through the stubbed vault`() {
    val plaintextKey = randomAesKey()
    val plaintextKeyB64 = Base64.getEncoder().encodeToString(plaintextKey)

    // create_data_key stub — the same key is returned as plaintext + opaque wrapped form.
    wireMockRule.stubFor(
      post(urlPathEqualTo("/vault/v1/keys/data-key"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"context":{"env":"t"},"data_key":"$plaintextKeyB64","encrypted_keys":"d3JhcHBlZA==","id":"dk_1"}"""
            )
        )
    )
    // decrypt_data_key stub — return the same plaintext key (happy path).
    wireMockRule.stubFor(
      post(urlPathEqualTo("/vault/v1/keys/decrypt"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""{"data_key":"$plaintextKeyB64","id":"dk_1"}""")
        )
    )

    val vault = api()
    val plaintext = "hello, vault"
    val encrypted = vault.encrypt(plaintext, mapOf("env" to "t"))
    val decrypted = vault.decrypt(encrypted)
    assertEquals(plaintext, decrypted)
  }

  @Test
  fun `decrypt validates against an externally produced payload`() {
    // Build a payload manually to confirm the wire format matches.
    val plaintextKey = randomAesKey()
    val iv = ByteArray(12).also { java.security.SecureRandom().nextBytes(it) }
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(plaintextKey, "AES"), GCMParameterSpec(128, iv))
    val plaintext = "round-trip"
    val out = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
    val ciphertext = out.copyOfRange(0, out.size - 16)
    val tag = out.copyOfRange(out.size - 16, out.size)

    val keyBlob = "blob-bytes".toByteArray()
    val lenPrefix = leb128(keyBlob.size)
    val combined =
      ByteArray(iv.size + tag.size + lenPrefix.size + keyBlob.size + ciphertext.size).apply {
        var off = 0
        iv.copyInto(this, off)
        off += iv.size
        tag.copyInto(this, off)
        off += tag.size
        lenPrefix.copyInto(this, off)
        off += lenPrefix.size
        keyBlob.copyInto(this, off)
        off += keyBlob.size
        ciphertext.copyInto(this, off)
      }
    val payloadB64 = Base64.getEncoder().encodeToString(combined)

    wireMockRule.stubFor(
      post(urlPathEqualTo("/vault/v1/keys/decrypt"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{"data_key":"${Base64.getEncoder().encodeToString(plaintextKey)}","id":"dk_1"}"""
            )
        )
    )
    val result = api().decrypt(payloadB64)
    assertEquals(plaintext, result)
  }

  @Test
  fun `readObject translates 401 to UnauthorizedException`() {
    wireMockRule.stubFor(
      get(urlPathMatching("/vault/v1/kv/[^/]+"))
        .willReturn(aResponse().withStatus(401).withBody("{}"))
    )
    assertThrows(UnauthorizedException::class.java) {
      api().readObject("vo_1")
    }
  }

  @Test
  fun `vault accessor is cached on the WorkOS client`() {
    val workos = createWorkOSClient()
    assertSame(workos.vault, workos.vault)
  }

  private fun randomAesKeyBase64(): String = Base64.getEncoder().encodeToString(randomAesKey())

  private fun randomAesKey(): ByteArray {
    val gen = KeyGenerator.getInstance("AES")
    gen.init(256)
    return gen.generateKey().encoded
  }

  private fun leb128(value: Int): ByteArray {
    var remaining = value
    val bytes = mutableListOf<Byte>()
    while (true) {
      var byte = remaining and 0x7F
      remaining = remaining ushr 7
      if (remaining != 0) byte = byte or 0x80
      bytes += byte.toByte()
      if (remaining == 0) break
    }
    return ByteArray(bytes.size) { bytes[it] }
  }
}
