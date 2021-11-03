package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonValue

enum class DirectoryState(@JsonValue val state: String) {
  InvalidCredentials("invalid_credentials"),
  Linked("linked"),
  Unlinked("unlinked"),
}
