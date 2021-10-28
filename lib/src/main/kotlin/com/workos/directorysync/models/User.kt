package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class User
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory_user",
  var id: String,
  @JsonProperty("idp_id")
  var idpId: String,
  @JsonProperty("username")
  var userName: String,
  @JsonProperty("first_name")
  var firstName: String,
  @JsonProperty("last_name")
  var lastName: String,
  var emails: MutableList<Email>,
  var groups: MutableList<Group>,
  var state: UserState,
  @JsonProperty("custom_attributes")
  val customAttributes: MutableMap<String, String>,
  @JsonProperty("raw_attributes")
  val rawAttributes: MutableMap<String, String>,
)

enum class UserState(@JsonValue val state: String) {
  Active("active"),
  Suspended("suspended")
}
