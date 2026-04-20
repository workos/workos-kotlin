// @oagen-ignore-file
// Hand-maintained session-cookie helpers. These are client-side conveniences
// layered on top of generated UserManagement endpoints + JWKS verification.
package com.workos.session

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.workos.WorkOS
import com.workos.common.json.ObjectMapperFactory
import com.workos.models.AuthenticateResponse
import com.workos.usermanagement.AuthKitLogoutUrlOptions
import com.workos.usermanagement.UserManagement
import com.workos.usermanagement.getLogoutUrl
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/** Reason an `authenticate()` call failed. */
enum class AuthenticateSessionFailureReason {
  /** No sealed session cookie was provided by the caller. */
  NO_SESSION_COOKIE_PROVIDED,

  /** The cookie was missing, malformed, expired, or could not be unsealed. */
  INVALID_SESSION_COOKIE,

  /** The access token inside the cookie failed JWKS verification. */
  INVALID_JWT
}

/** Reason a `refresh()` call failed. */
enum class RefreshSessionFailureReason {
  /** The cookie was missing, malformed, expired, or could not be unsealed. */
  INVALID_SESSION_COOKIE,

  /** The refresh token exchange was rejected by WorkOS as invalid. */
  INVALID_GRANT,

  /** The refresh flow requires the user to complete MFA enrollment first. */
  MFA_ENROLLMENT,

  /** The refresh flow requires an SSO re-authentication step. */
  SSO_REQUIRED
}

/** Payload persisted in a sealed session cookie. */
data class SessionCookieData(
  val accessToken: String,
  val refreshToken: String,
  val user: Map<String, Any?>? = null,
  val authenticationMethod: String? = null,
  val impersonator: Map<String, Any?>? = null
)

/** Successful `authenticate()` response. */
data class AuthenticateSessionSuccess(
  val sessionId: String,
  val organizationId: String? = null,
  val role: String? = null,
  val roles: List<String>? = null,
  val permissions: List<String>? = null,
  val entitlements: List<String>? = null,
  val featureFlags: List<String>? = null,
  val user: Map<String, Any?>? = null,
  val authenticationMethod: String? = null,
  val impersonator: Map<String, Any?>? = null,
  val accessToken: String
)

/** Failed `authenticate()` response. */
data class AuthenticateSessionFailure(
  val reason: AuthenticateSessionFailureReason
)

/** Sum type returned by [SessionCookie.authenticate]. */
sealed class AuthenticateSessionResult {
  data class Success(
    val sessionId: String,
    val organizationId: String? = null,
    val role: String? = null,
    val roles: List<String>? = null,
    val permissions: List<String>? = null,
    val entitlements: List<String>? = null,
    val featureFlags: List<String>? = null,
    val user: Map<String, Any?>? = null,
    val authenticationMethod: String? = null,
    val impersonator: Map<String, Any?>? = null,
    val accessToken: String
  ) : AuthenticateSessionResult()

  data class Failure(
    val reason: AuthenticateSessionFailureReason
  ) : AuthenticateSessionResult()

  val authenticated: Boolean
    get() = this is Success

  fun getSuccess(): AuthenticateSessionSuccess? =
    (this as? Success)?.let {
      AuthenticateSessionSuccess(
        sessionId = it.sessionId,
        organizationId = it.organizationId,
        role = it.role,
        roles = it.roles,
        permissions = it.permissions,
        entitlements = it.entitlements,
        featureFlags = it.featureFlags,
        user = it.user,
        authenticationMethod = it.authenticationMethod,
        impersonator = it.impersonator,
        accessToken = it.accessToken
      )
    }

  fun getFailure(): AuthenticateSessionFailure? = (this as? Failure)?.let { AuthenticateSessionFailure(it.reason) }
}

/** Successful `refresh()` response. */
data class RefreshSessionSuccess(
  val sealedSession: String,
  val sessionId: String,
  val organizationId: String? = null,
  val role: String? = null,
  val roles: List<String>? = null,
  val permissions: List<String>? = null,
  val entitlements: List<String>? = null,
  val featureFlags: List<String>? = null,
  val user: Map<String, Any?>? = null,
  val authenticationMethod: String? = null,
  val impersonator: Map<String, Any?>? = null
)

/** Failed `refresh()` response. */
data class RefreshSessionFailure(
  val reason: RefreshSessionFailureReason
)

/** Sum type returned by [SessionCookie.refresh]. */
sealed class RefreshSessionResult {
  data class Success(
    val value: RefreshSessionSuccess
  ) : RefreshSessionResult()

  data class Failure(
    val value: RefreshSessionFailure
  ) : RefreshSessionResult()

  val authenticated: Boolean
    get() = this is Success
}

/**
 * Stateful session-cookie helper (H04). Construct one per request with the
 * inbound sealed cookie + the password used to seal it. Use
 * [authenticate] to validate the cookie and decode claims; [refresh] to
 * exchange the embedded refresh token for a new sealed session;
 * [getLogoutUrl] to derive the logout URL.
 */
class SessionCookie
  @JvmOverloads
  constructor(
    private val userManagement: UserManagement,
    sessionData: String?,
    private var cookiePassword: String,
    private val objectMapper: ObjectMapper = defaultObjectMapper()
  ) {
    init {
      require(cookiePassword.isNotEmpty()) { "cookiePassword is required" }
    }

    private var sessionData: String? = sessionData

    /** Validate the sealed cookie and, if valid, decode its JWT access-token claims. */
    fun authenticate(): AuthenticateSessionResult {
      val data = sessionData
      if (data.isNullOrEmpty()) {
        return AuthenticateSessionResult.Failure(AuthenticateSessionFailureReason.NO_SESSION_COOKIE_PROVIDED)
      }

      val session =
        try {
          unsealSessionData(data, cookiePassword, objectMapper)
        } catch (_: IronException) {
          return AuthenticateSessionResult.Failure(AuthenticateSessionFailureReason.INVALID_SESSION_COOKIE)
        }

      if (session.accessToken.isEmpty()) {
        return AuthenticateSessionResult.Failure(AuthenticateSessionFailureReason.INVALID_SESSION_COOKIE)
      }

      if (!Jwks.isValidJwt(userManagement.workos, session.accessToken)) {
        return AuthenticateSessionResult.Failure(AuthenticateSessionFailureReason.INVALID_JWT)
      }

      val claims = SignedJWT.parse(session.accessToken).jwtClaimsSet
      @Suppress("UNCHECKED_CAST")
      return AuthenticateSessionResult.Success(
        sessionId = claims.getStringClaim("sid") ?: "",
        organizationId = claims.getStringClaim("org_id"),
        role = claims.getStringClaim("role"),
        roles = claims.getStringListClaim("roles"),
        permissions = claims.getStringListClaim("permissions"),
        entitlements = claims.getStringListClaim("entitlements"),
        featureFlags = claims.getStringListClaim("feature_flags"),
        user = session.user,
        authenticationMethod = session.authenticationMethod,
        impersonator = session.impersonator,
        accessToken = session.accessToken
      )
    }

    /**
     * Exchange the embedded refresh token for a new access token, reseal the
     * updated session, and update this helper's state so subsequent
     * [authenticate] calls see the new token.
     */
    @JvmOverloads
    fun refresh(
      organizationId: String? = null,
      newCookiePassword: String? = null
    ): RefreshSessionResult {
      val data =
        sessionData
          ?: return RefreshSessionResult.Failure(
            RefreshSessionFailure(RefreshSessionFailureReason.INVALID_SESSION_COOKIE)
          )
      val session =
        try {
          unsealSessionData(data, cookiePassword, objectMapper)
        } catch (_: IronException) {
          return RefreshSessionResult.Failure(
            RefreshSessionFailure(RefreshSessionFailureReason.INVALID_SESSION_COOKIE)
          )
        }
      if (session.refreshToken.isEmpty()) {
        return RefreshSessionResult.Failure(
          RefreshSessionFailure(RefreshSessionFailureReason.INVALID_SESSION_COOKIE)
        )
      }

      val effectiveOrgId =
        organizationId
          ?: runCatching { SignedJWT.parse(session.accessToken).jwtClaimsSet.getStringClaim("org_id") }.getOrNull()

      val response =
        try {
          userManagement.authenticateWithRefreshToken(
            refreshToken = session.refreshToken,
            organizationId = effectiveOrgId
          )
        } catch (e: com.workos.common.exceptions.WorkOSException) {
          return mapOauthError(e) ?: throw e
        }

      val newPassword = newCookiePassword ?: cookiePassword
      val newSession =
        SessionCookieData(
          accessToken = response.accessToken,
          refreshToken = response.refreshToken,
          user = session.user,
          authenticationMethod = response.authenticationMethod?.value ?: session.authenticationMethod,
          impersonator = session.impersonator
        )
      val sealed = Iron.seal(objectMapper.writeValueAsString(newSession), newPassword)
      this.sessionData = sealed
      this.cookiePassword = newPassword

      val claims = SignedJWT.parse(response.accessToken).jwtClaimsSet
      return RefreshSessionResult.Success(
        RefreshSessionSuccess(
          sealedSession = sealed,
          sessionId = claims.getStringClaim("sid") ?: "",
          organizationId = claims.getStringClaim("org_id"),
          role = claims.getStringClaim("role"),
          roles = claims.getStringListClaim("roles"),
          permissions = claims.getStringListClaim("permissions"),
          entitlements = claims.getStringListClaim("entitlements"),
          featureFlags = claims.getStringListClaim("feature_flags"),
          user = session.user,
          authenticationMethod = newSession.authenticationMethod,
          impersonator = session.impersonator
        )
      )
    }

    /** Build the AuthKit logout URL for the current session. */
    @JvmOverloads
    fun getLogoutUrl(returnTo: String? = null): String {
      val auth = authenticate()
      val success =
        when (auth) {
          is AuthenticateSessionResult.Success -> auth
          is AuthenticateSessionResult.Failure ->
            throw IllegalStateException("Failed to extract session ID for logout URL: ${auth.reason}")
        }
      return userManagement.getLogoutUrl(
        AuthKitLogoutUrlOptions(sessionId = success.sessionId, returnTo = returnTo)
      )
    }

    private fun mapOauthError(e: com.workos.common.exceptions.WorkOSException): RefreshSessionResult.Failure? {
      val code = e.code ?: return null
      val reason =
        when (code) {
          "invalid_grant" -> RefreshSessionFailureReason.INVALID_GRANT
          "mfa_enrollment" -> RefreshSessionFailureReason.MFA_ENROLLMENT
          "sso_required" -> RefreshSessionFailureReason.SSO_REQUIRED
          else -> return null
        }
      return RefreshSessionResult.Failure(RefreshSessionFailure(reason))
    }
  }

/**
 * Top-level session helpers (H05, H06, H07). Available as `workos.session`.
 */
class Session internal constructor(
  private val workos: WorkOS
) {
  private val objectMapper = defaultObjectMapper()

  /** Build a [SessionCookie] handler for an inbound sealed-cookie value. */
  fun loadSealedSession(
    sessionData: String?,
    cookiePassword: String
  ): SessionCookie = SessionCookie(UserManagement(workos), sessionData, cookiePassword, objectMapper)

  /** Convenience: authenticate a sealed cookie in one call. */
  fun authenticateWithSessionCookie(
    sessionData: String?,
    cookiePassword: String
  ): AuthenticateSessionResult = loadSealedSession(sessionData, cookiePassword).authenticate()

  /** Convenience: refresh a sealed cookie in one call. */
  @JvmOverloads
  fun refreshSession(
    sessionData: String?,
    cookiePassword: String,
    organizationId: String? = null,
    newCookiePassword: String? = null
  ): RefreshSessionResult = loadSealedSession(sessionData, cookiePassword).refresh(organizationId, newCookiePassword)

  /** Raw Iron seal of an arbitrary JSON-serializable map (H06). */
  fun sealData(
    data: Map<String, Any?>,
    cookiePassword: String
  ): String = Iron.seal(objectMapper.writeValueAsString(data), cookiePassword)

  /** Raw Iron unseal. Returns an empty map when the seal is bad/expired (matches Node semantics). */
  fun unsealData(
    sealedData: String,
    cookiePassword: String
  ): Map<String, Any?> =
    try {
      val json = Iron.unseal(sealedData, cookiePassword)
      @Suppress("UNCHECKED_CAST")
      objectMapper.readValue<Map<String, Any?>>(json)
    } catch (_: IronException) {
      emptyMap()
    }

  /** Seal an [AuthenticateResponse] directly into a session cookie (H07). */
  fun sealAuthResponse(
    response: AuthenticateResponse,
    cookiePassword: String
  ): String {
    val payload =
      SessionCookieData(
        accessToken = response.accessToken,
        refreshToken = response.refreshToken,
        user = toMap(response.user),
        authenticationMethod = response.authenticationMethod?.value,
        impersonator = response.impersonator?.let { toMap(it) }
      )
    return Iron.seal(objectMapper.writeValueAsString(payload), cookiePassword)
  }

  @Suppress("UNCHECKED_CAST")
  private fun toMap(value: Any?): Map<String, Any?>? {
    if (value == null) return null
    return objectMapper.convertValue(value, Map::class.java) as? Map<String, Any?>
  }
}

/** Hand-maintained accessor on the WorkOS client. */
val WorkOS.session: Session
  get() = service(Session::class) { Session(this) }

internal fun defaultObjectMapper(): ObjectMapper = ObjectMapperFactory.default

internal fun unsealSessionData(
  sealed: String,
  password: String,
  mapper: ObjectMapper
): SessionCookieData {
  val json = Iron.unseal(sealed, password)
  return mapper.readValue(json, SessionCookieData::class.java)
}

/** Tiny wrapper around Nimbus JOSE + cached JWKS per WorkOS client. */
internal object Jwks {
  private val caches = ConcurrentHashMap<String, DefaultJWTProcessor<SecurityContext>>()

  fun isValidJwt(
    workos: WorkOS,
    accessToken: String
  ): Boolean {
    val clientId = workos.clientId ?: error("Missing client ID. Did you provide it when initializing WorkOS?")
    val processor =
      caches.computeIfAbsent("${workos.apiBaseUrl}|$clientId") {
        val url = URL("${workos.apiBaseUrl.trimEnd('/')}/sso/jwks/$clientId")
        val jwkSource = JWKSourceBuilder.create<SecurityContext>(url).build()
        DefaultJWTProcessor<SecurityContext>().apply {
          jwsKeySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)
        }
      }
    return try {
      processor.process(SignedJWT.parse(accessToken), null)
      true
    } catch (_: Exception) {
      false
    }
  }
}
