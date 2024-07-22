package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.usermanagement.types.AuthenticationEventTypeEnumType
import com.workos.usermanagement.types.AuthenticationEventStatusEnumType

/**
 * Authentication events are emitted when a user attempts authentication using WorkOS.
 *
 * @param type The type of authentication event (see enum values in [AuthenticationEventTypeEnumType])
 * @param status The status of the authentication event (see enum values in [AuthenticationEventStatusEnumType]).
 * @param email The email address of the user attempting authentication, if available.
 * @param userId The ID of the user attempting authentication, if available.
 * @param ipAddress The IP address of the authentication attempt, if available.
 * @param userAgent The user agent of the authentication attempy, if available.
 * @param error Error details if the event is for a failed authentication attempt.
 */
data class AuthenticationEventData @JsonCreator constructor(
  @JsonProperty("type")
  val type: AuthenticationEventTypeEnumType,
  
  @JsonProperty("status")
  val status: AuthenticationEventStatusEnumType,

  @JsonProperty("email")
  val email: String? = null,

  @JsonProperty("user_id")
  val userId: String? = null,

  @JsonProperty("ip_address")
  val ipAddress: String? = null,

  @JsonProperty("user_agent")
  val userAgent: String? = null,

  @JsonProperty("error")
  val error: AuthenticationEventDataError? = null
)
