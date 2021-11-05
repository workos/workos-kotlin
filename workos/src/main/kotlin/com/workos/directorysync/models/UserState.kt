package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

enum class UserState(@JsonValue val state: String) {
  Active("active"),
  Suspended("suspended")
  Inactive("inactive")
}
