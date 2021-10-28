package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class User
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory_user",

  val id: String,

  @JsonProperty("idp_id")
  val idpId: String,

  @JsonProperty("username")
  val userName: String,

  @JsonProperty("first_name")
  val firstName: String,

  @JsonProperty("last_name")
  val lastName: String,

  val emails: MutableList<Email>,

  val groups: MutableList<Group>,

  val state: UserState,

  @JsonProperty("custom_attributes")
  val customAttributes: MutableMap<String, String>,

  @JsonProperty("raw_attributes")
  val rawAttributes: MutableMap<String, String>,
)

enum class UserState(@JsonValue val state: String) {
  Active("active"),
  Suspended("suspended")
}
