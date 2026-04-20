package com.workos.usermanagement.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A response containing a WorkOS user.
 *
 * @param user The corresponding user object.
 */
data class UserResponse @JsonCreator constructor(

  @JsonProperty("user")
  val user: User,
)
