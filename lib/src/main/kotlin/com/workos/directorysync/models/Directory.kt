package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class Directory
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory",

  val id: String,

  val name: String,

  val domain: String,

  @JsonProperty("environment_id")
  val environmentId: String?,

  @JsonProperty("external_key")
  val externalKey: String?,

  @JsonProperty("organization_id")
  val organizationId: String?,

  val state: DirectoryState,

  val type: DirectoryType,

  @JsonProperty("created_at")
  val createdAt: String,

  @JsonProperty("updated_at")
  val updatedAt: String,
)

enum class DirectoryState(@JsonValue val state: String) {
  InvalidCredentials("invalid_credentials"),
  Linked("linked"),
  Unlinked("unlinked"),
}

enum class DirectoryType(@JsonValue val type: String) {
  AzureSCIMV2_0("azure scim v2.0"),
  BambooHr("bamboohr"),
  GenericSCIMV1_1("generic scim v1.1"),
  GenericSCIMV2_0("generic scim v2.0"),
  GSuiteDirectory("gsuite directory"),
  JumpCloudSCIM2_0("jump cloud scim v2.0"),
  OktaSCIMV1_1("okta scim v1.1"),
  OktaSCIMV2_0("okta scim v2.0"),
  Rippling("rippling"),
  Workday("workday"),
}
