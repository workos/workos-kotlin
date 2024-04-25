package com.workos.usermanagement.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class PasswordHashTypeEnumType(val type: String) {
  /**
   * Bcrypt
   */
  @JsonProperty("bcrypt")
  Bcrypt("bcrypt"),

  /**
   * Firebase-scrypt
   */
  @JsonProperty("firebase-scrypt")
  FirebaseScrypt("firebase-scrypt"),

  /**
   * Ssha
   */
  @JsonProperty("ssha")
  Ssha("ssha"),
}
