package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User
    @JsonCreator constructor(
        @JsonProperty("object")
        val obj: String = "directory_user",
        @JsonProperty("id")
        var id: String,
        @JsonProperty("idp_id")
        var idpId: String,
        @JsonProperty("username")
        var userName: String,
        @JsonProperty("first_name")
        var firstName: String,
        @JsonProperty("last_name")
        var lastName: String,
        @JsonProperty("emails")
        var emails: MutableList<Email>,
        @JsonProperty("groups")
        var groups: MutableList<Group>,
        @JsonProperty("state")
        var state: UserState,
        @JsonProperty("custom_attributes")
        val custom_attributes: MutableMap<String, String>,
        @JsonProperty("raw_attributes")
        val raw_attributes: MutableMap<String, String>,
)

enum class UserState(val state: String) {
    Active("active"),
    Suspended("suspended")
}