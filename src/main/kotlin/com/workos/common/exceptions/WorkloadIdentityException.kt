// @oagen-ignore-file
package com.workos.common.exceptions

private const val DESCRIPTION_LIMIT = 500

private const val UNKNOWN_OAUTH_ERROR_HINT = "The token endpoint rejected the exchange."

/**
 * The one table of hints, keyed by error code. The first six codes come from
 * the token endpoint; the last four are raised by the SDK for failures that
 * have no OAuth code. Any other code gets [UNKNOWN_OAUTH_ERROR_HINT].
 */
private val HINTS: Map<String, String> =
  mapOf(
    "invalid_grant" to
      "The identity token was not accepted. Check its issuer, audience and subject claims against the trust " +
      "rule, and that it has not expired. A token that was already exchanged is also refused: each exchange " +
      "needs a new identity token.",
    "invalid_target" to
      "The resource is not one the token endpoint accepts. Leave the resource unset to use the default, " +
      "https://api.workos.com.",
    "invalid_client" to
      "The client ID is unknown, or the application has no enabled trust rule. Check that the client ID is " +
      "the M2M application that has the trust rule.",
    "unauthorized_client" to
      "This application cannot use workload identity federation. Check that the feature is enabled for your " +
      "team and that the application is bound to a non-production environment.",
    "invalid_scope" to "The requested scope is not allowed for this application. Fix the scope or leave it unset.",
    "invalid_request" to
      "The token request was malformed. Check that the token URL points at the `/oauth2/token` endpoint.",
    WorkloadIdentityException.EXCHANGE_FAILED to
      "The token endpoint could not be reached or kept failing. Check the token URL and try again.",
    WorkloadIdentityException.INVALID_RESPONSE to
      "The token endpoint returned a response the SDK could not read. " +
      "Check that the token URL points at the `/oauth2/token` endpoint.",
    WorkloadIdentityException.PROVIDER_FAILED to
      "The identity token provider failed, so there was no token to exchange.",
    WorkloadIdentityException.INVALID_CONFIGURATION to "Workload identity credentials are missing a required setting."
  )

private fun hintFor(code: String): String = HINTS[code] ?: UNKNOWN_OAUTH_ERROR_HINT

private fun cut(description: String): String = description.take(DESCRIPTION_LIMIT)

private fun messageFor(
  code: String,
  description: String
): String {
  val shortened = cut(description)
  return if (shortened.isEmpty()) "$code: ${hintFor(code)}" else "$code: ${hintFor(code)} $shortened"
}

/**
 * Raised when workload identity credentials cannot produce an access token:
 * the configuration is incomplete, the identity token provider failed, or the
 * token endpoint refused or could not complete the exchange.
 *
 * [code] is the OAuth error code from the token endpoint (for example
 * `invalid_grant`), or one of [EXCHANGE_FAILED], [INVALID_RESPONSE],
 * [PROVIDER_FAILED] and [INVALID_CONFIGURATION] for failures the SDK detects
 * itself. The message is `<code>: <hint>`, followed by [description] when
 * there is one. Neither the identity token nor the access token ever appears
 * in the message or in any field.
 */
class WorkloadIdentityException internal constructor(
  code: String,
  description: String,
  status: Int = 0,
  cause: Throwable? = null
) : WorkOSException(
    status = status,
    requestId = null,
    code = code,
    message = messageFor(code, description),
    errors = null,
    rawBody = null,
    cause = cause
  ) {
  /** The token endpoint's `error_description`, cut to 500 characters, or the SDK's own detail. Empty when there is none. */
  @JvmField val description: String = cut(description)

  /** One sentence on what to check. Never empty. */
  @JvmField val hint: String = hintFor(code)

  /** Error codes the SDK raises for failures that have no OAuth error code. */
  companion object {
    /** The token endpoint could not be reached, kept failing, or answered with a redirect. */
    const val EXCHANGE_FAILED: String = "exchange_failed"

    /** The token endpoint answered with something that is not a token response. */
    const val INVALID_RESPONSE: String = "invalid_response"

    /** The identity token provider threw or returned an empty token. */
    const val PROVIDER_FAILED: String = "provider_failed"

    /** The credentials are missing a required setting. */
    const val INVALID_CONFIGURATION: String = "invalid_configuration"
  }
}
