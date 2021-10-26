package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Directory
    @JsonCreator constructor(
        @JsonProperty("object")
        val obj: String = "directory",
        @JsonProperty("id")
        var id: String,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("domain")
        var domain: String,
        @JsonProperty("environment_id")
        var environmentId: String,
        @JsonProperty("external_key")
        var externalKey: String,
        @JsonProperty("organization_id")
        var organization_id: String,
        @JsonProperty("state")
        var state: DirectoryState,
        @JsonProperty("type")
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
    AzureScimV2_0("azure scim v2.0"),
    GenericScimV1_1("generic scim v1.1"),
    GenericScimV2_0("generic scim v2.0"),
    OktaScimV1_1("okta scim v1.1"),
    OktaScimV2_0("okta scim v2.0"),
    BambooHr("bamboohr"),
    Rippling("rippling"),
    GoogleWorkspace("gsuite directory"),
    Workday("workday"),
}