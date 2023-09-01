package com.workos.users.models

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonValue


enum class FactorType(@JsonValue val type: String) {

  TOTP("totp"),


}
