package com.workos.portal.models

import com.fasterxml.jackson.annotation.JsonValue

enum class Intent(@JsonValue val value: String) {
  Sso("sso"),
  DirectorySync("dsync")
}
