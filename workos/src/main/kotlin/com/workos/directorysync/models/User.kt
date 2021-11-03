package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "directory_user",

  @JvmField
  val id: String,

  @JvmField
  @JsonProperty("directory_id")
  val directoryId: String,

  @JvmField
  @JsonProperty("idp_id")
  val idpId: String,

  @JvmField
  @JsonProperty("username")
  val userName: String?,

  @JvmField
  @JsonProperty("first_name")
  val firstName: String?,

  @JvmField
  @JsonProperty("last_name")
  val lastName: String?,

  @JvmField
  val emails: List<Email>,

  @JvmField
  val groups: List<Group>,

  @JvmField
  val state: UserState,

  @JvmField
  @JsonProperty("custom_attributes")
  val customAttributes: Map<String, String>,

  @JvmField
  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, Any>,
)
