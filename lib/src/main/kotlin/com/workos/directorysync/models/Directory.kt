package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Directory
@JsonCreator constructor(
    @JsonProperty("object")
    val obj: String = "directory",
    var id: String,
    var name: String,
    var domain: String,
    @JsonProperty("environment_id")
    var environmentId: String,
    @JsonProperty("external_key")
    var externalKey: String,
    @JsonProperty("organization_id")
    var organizationId: String,
    @JsonProperty("state")
    var state: DirectoryState,
    var type: DirectoryType,
    @JsonProperty("created_at")
    var createdAt: String,
    @JsonProperty("updated_at")
    var updatedAt: String,
)

enum class DirectoryState(val state: String) {
    InvalidCredentials("invalid_credentials"),
    Linked("linked"),
    Unlinked("unlinked"),
}

enum class DirectoryType(val type: String) {
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
