// @oagen-ignore-file
package com.workos.session

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.workos.WorkOS
import com.workos.common.http.RetryConfig
import com.workos.common.json.ObjectMapperFactory
import com.workos.test.TestBase
import com.workos.usermanagement.UserManagement
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

class SessionTest : TestBase() {
  private val cookiePassword = "this-is-at-least-thirty-two-chars!"
  private val mapper = ObjectMapperFactory.create()

  /**
   * Distinct client ID per test so the [Jwks] processor cache keyed on
   * `(baseUrl, clientId)` doesn't smear state across tests that stub a
   * different JWKS body.
   */
  private fun workOSForTest(): WorkOS {
    val clientId = "client_${UUID.randomUUID().toString().replace("-", "").take(10)}"
    val http =
      OkHttpClient
        .Builder()
        .callTimeout(5, TimeUnit.SECONDS)
        .build()
    return WorkOS(
      apiKey = "sk_test_example",
      clientId = clientId,
      apiBaseUrl = wireMockRule.baseUrl(),
      httpClient = http,
      retryConfig = RetryConfig.DISABLED
    )
  }

  private fun clientIdOf(workos: WorkOS): String = workos.clientId!!

  @Test
  fun `authenticate returns NO_SESSION_COOKIE_PROVIDED when cookie is empty`() {
    val workos = workOSForTest()
    val result = workos.session.authenticateWithSessionCookie(null, cookiePassword)
    val failure = result as AuthenticateSessionResult.Failure
    assertEquals(AuthenticateSessionFailureReason.NO_SESSION_COOKIE_PROVIDED, failure.reason)
  }

  @Test
  fun `authenticate returns INVALID_SESSION_COOKIE for a malformed seal`() {
    val workos = workOSForTest()
    val result = workos.session.authenticateWithSessionCookie("not-a-seal", cookiePassword)
    val failure = result as AuthenticateSessionResult.Failure
    assertEquals(AuthenticateSessionFailureReason.INVALID_SESSION_COOKIE, failure.reason)
  }

  @Test
  fun `authenticate succeeds for a valid session cookie against a real JWKS`() {
    val workos = workOSForTest()
    val rsaKey = RSAKeyGenerator(2048).keyID("k1").keyUse(KeyUse.SIGNATURE).generate()
    stubJwks(clientIdOf(workos), jwksJson(rsaKey))

    val accessToken = signJwt(rsaKey, sessionId = "sess_123", orgId = "org_1")
    val cookieJson =
      mapper.writeValueAsString(
        mapOf(
          "accessToken" to accessToken,
          "refreshToken" to "refresh_1",
          "user" to mapOf("id" to "user_1")
        )
      )
    val sealed = Iron.seal(cookieJson, cookiePassword)

    val result = workos.session.authenticateWithSessionCookie(sealed, cookiePassword)
    val success = result as AuthenticateSessionResult.Success
    assertEquals("sess_123", success.sessionId)
    assertEquals("org_1", success.organizationId)
    assertEquals(accessToken, success.accessToken)
    assertNotNull(result.getSuccess())
  }

  @Test
  fun `authenticate returns INVALID_JWT when JWKS rejects the token`() {
    val workos = workOSForTest()
    val rsaKey = RSAKeyGenerator(2048).keyID("k1").keyUse(KeyUse.SIGNATURE).generate()
    val otherKey = RSAKeyGenerator(2048).keyID("k1").keyUse(KeyUse.SIGNATURE).generate()
    stubJwks(clientIdOf(workos), jwksJson(otherKey))

    val accessToken = signJwt(rsaKey, sessionId = "sess_x", orgId = null)
    val cookieJson =
      mapper.writeValueAsString(
        mapOf("accessToken" to accessToken, "refreshToken" to "r")
      )
    val sealed = Iron.seal(cookieJson, cookiePassword)
    val result = workos.session.authenticateWithSessionCookie(sealed, cookiePassword)
    val failure = result as AuthenticateSessionResult.Failure
    assertEquals(AuthenticateSessionFailureReason.INVALID_JWT, failure.reason)
    assertNotNull(result.getFailure())
  }

  @Test
  fun `refresh exchanges the token, reseals, and returns success`() {
    val workos = workOSForTest()
    val rsaKey = RSAKeyGenerator(2048).keyID("k1").keyUse(KeyUse.SIGNATURE).generate()
    val newAccessToken = signJwt(rsaKey, sessionId = "sess_new", orgId = "org_new")
    stubJwks(clientIdOf(workos), jwksJson(rsaKey))
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              mapper.writeValueAsString(
                mapOf(
                  "user" to
                    mapOf(
                      "object" to "user",
                      "id" to "user_1",
                      "email" to "user@example.com",
                      "first_name" to null,
                      "last_name" to null,
                      "email_verified" to true,
                      "profile_picture_url" to null,
                      "last_sign_in_at" to null,
                      "created_at" to "2024-01-01T00:00:00Z",
                      "updated_at" to "2024-01-01T00:00:00Z",
                      "external_id" to null,
                      "metadata" to emptyMap<String, Any>()
                    ),
                  "access_token" to newAccessToken,
                  "refresh_token" to "refresh_new",
                  "organization_id" to "org_new"
                )
              )
            )
        )
    )

    val oldCookieJson =
      mapper.writeValueAsString(
        mapOf(
          "accessToken" to "old.access.token",
          "refreshToken" to "refresh_old"
        )
      )
    val oldSealed = Iron.seal(oldCookieJson, cookiePassword)

    val helper = workos.session.loadSealedSession(oldSealed, cookiePassword)
    val refreshed = helper.refresh(organizationId = "org_new")
    val success = (refreshed as RefreshSessionResult.Success).value
    assertEquals("sess_new", success.sessionId)
    assertEquals("org_new", success.organizationId)
    assertNotNull(success.sealedSession)

    // After refresh, authenticate should succeed with the new sealed session.
    val reauth = helper.authenticate()
    assertTrue(reauth is AuthenticateSessionResult.Success)
  }

  @Test
  fun `sealData roundtrips through unsealData`() {
    val workos = workOSForTest()
    val sealed = workos.session.sealData(mapOf("a" to 1, "b" to "two"), cookiePassword)
    val out = workos.session.unsealData(sealed, cookiePassword)
    assertEquals(1, out["a"])
    assertEquals("two", out["b"])
  }

  @Test
  fun `unsealData returns an empty map for a bad seal`() {
    val workos = workOSForTest()
    val out = workos.session.unsealData("garbage", cookiePassword)
    assertTrue(out.isEmpty())
  }

  @Test
  fun `sealAuthResponse can be unsealed into SessionCookieData`() {
    val workos = workOSForTest()
    // Build an AuthenticateResponse via Jackson so we don't need to hand-build models.
    val responseJson =
      """
      {
        "user": {
          "object": "user",
          "id": "user_1",
          "email": "u@e.com",
          "first_name": null,
          "last_name": null,
          "email_verified": true,
          "profile_picture_url": null,
          "last_sign_in_at": null,
          "created_at": "2024-01-01T00:00:00Z",
          "updated_at": "2024-01-01T00:00:00Z",
          "external_id": null,
          "metadata": {}
        },
        "access_token": "a.b.c",
        "refresh_token": "r"
      }
      """.trimIndent()
    val response = mapper.readValue(responseJson, com.workos.models.AuthenticateResponse::class.java)
    val sealed = workos.session.sealAuthResponse(response, cookiePassword)
    val plain = workos.session.unsealData(sealed, cookiePassword)
    assertEquals("a.b.c", plain["accessToken"])
    assertEquals("r", plain["refreshToken"])
  }

  @Test
  fun `session accessor is cached on the WorkOS client`() {
    val workos = workOSForTest()
    assertTrue(workos.session === workos.session)
  }

  @Test
  fun `SessionCookie rejects empty passwords`() {
    try {
      SessionCookie(UserManagement(workOSForTest()), "s", "")
      error("expected IllegalArgumentException")
    } catch (e: IllegalArgumentException) {
      assertFalse(e.message.isNullOrEmpty())
    }
  }

  private fun stubJwks(
    clientId: String,
    jwksJson: String
  ) {
    wireMockRule.stubFor(
      get(urlPathEqualTo("/sso/jwks/$clientId"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(jwksJson)
        )
    )
  }

  private fun signJwt(
    key: RSAKey,
    sessionId: String,
    orgId: String?
  ): String {
    val claimsBuilder =
      JWTClaimsSet
        .Builder()
        .issuer("https://api.workos.com/")
        .subject("user_1")
        .jwtID(UUID.randomUUID().toString())
        .issueTime(Date())
        .expirationTime(Date(System.currentTimeMillis() + 60_000))
        .claim("sid", sessionId)
    if (orgId != null) claimsBuilder.claim("org_id", orgId)
    val jwt =
      SignedJWT(
        JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.keyID).build(),
        claimsBuilder.build()
      )
    jwt.sign(RSASSASigner(key))
    return jwt.serialize()
  }

  private fun jwksJson(key: RSAKey): String {
    val pub = key.toPublicJWK()
    return """{"keys":[${pub.toJSONString()}]}"""
  }
}
