// @oagen-ignore-file
// Hand-maintained device-authorization polling helper (H12). Polls the
// token-exchange endpoint per RFC 8628 until success or a terminal error.
package com.workos.usermanagement

import com.workos.WorkOS
import com.workos.common.exceptions.WorkOSException
import com.workos.models.AuthenticateResponse

/** Result of a successful [UserManagement.pollDeviceAuthorization] call. */
typealias DeviceAuthenticationResponse = AuthenticateResponse

/** Why a device-flow poll gave up. */
enum class DeviceFlowFailureReason {
  ACCESS_DENIED,
  EXPIRED_TOKEN,
  POLLING_TIMED_OUT
}

/** Thrown when the user denies the device or the code expires. */
class DeviceFlowException(
  /** The category of failure that ended the device-flow poll. */
  val reason: DeviceFlowFailureReason,
  message: String
) : RuntimeException(message)

/** Options for [UserManagement.pollDeviceAuthorization]. */
data class PollDeviceAuthorizationOptions
  @JvmOverloads
  constructor(
    /** The `device_code` returned by `createDevice`. */
    val deviceCode: String,
    /** Initial polling interval in seconds (typically from `DeviceAuthorizationResponse.interval`). */
    val intervalSeconds: Int = 5,
    /** Maximum total time to poll, in seconds (typically from `expires_in`). */
    val expiresInSeconds: Int = 300,
    /** IP address of the device, forwarded to the authentication endpoint. */
    val ipAddress: String? = null,
    /** An identifier for the device, forwarded to the authentication endpoint. */
    val deviceId: String? = null,
    /** The user-agent string of the device, forwarded to the authentication endpoint. */
    val userAgent: String? = null
  )

/**
 * Poll the token-exchange endpoint until the user authorizes the device or
 * a terminal error occurs. Handles `authorization_pending` (continue polling),
 * `slow_down` (add 5s to the interval), `access_denied` (throw), and
 * `expired_token` (throw) per RFC 8628.
 *
 * Throws [DeviceFlowException] for terminal failures and the usual
 * [com.workos.common.exceptions.WorkOSException] hierarchy for other errors.
 */
fun UserManagement.pollDeviceAuthorization(options: PollDeviceAuthorizationOptions): DeviceAuthenticationResponse {
  val deadline = System.currentTimeMillis() + options.expiresInSeconds * 1_000L
  var interval = options.intervalSeconds.coerceAtLeast(1) * 1_000L
  while (true) {
    try {
      return authenticateWithDeviceCode(
        deviceCode = options.deviceCode,
        ipAddress = options.ipAddress,
        deviceId = options.deviceId,
        userAgent = options.userAgent
      )
    } catch (e: WorkOSException) {
      when (e.code) {
        "authorization_pending" -> {
          // keep waiting
        }
        "slow_down" -> {
          interval += 5_000L
        }
        "access_denied" ->
          throw DeviceFlowException(
            DeviceFlowFailureReason.ACCESS_DENIED,
            "User denied the device authorization"
          )
        "expired_token" ->
          throw DeviceFlowException(
            DeviceFlowFailureReason.EXPIRED_TOKEN,
            "Device code expired before the user authorized it"
          )
        else -> throw e
      }
    }
    if (System.currentTimeMillis() + interval > deadline) {
      throw DeviceFlowException(
        DeviceFlowFailureReason.POLLING_TIMED_OUT,
        "Device-flow polling exceeded expiresIn (${options.expiresInSeconds}s)"
      )
    }
    sleepFor(interval)
  }
}

/** Overridable sleep hook so tests can stub out real clock waits. */
@JvmField
internal var sleepFor: (Long) -> Unit = { Thread.sleep(it) }

/**
 * Convenience entrypoint on the WorkOS client. Equivalent to
 * `userManagement.pollDeviceAuthorization(options)`.
 */
fun WorkOS.pollDeviceAuthorization(options: PollDeviceAuthorizationOptions): DeviceAuthenticationResponse =
  UserManagement(this).pollDeviceAuthorization(options)
