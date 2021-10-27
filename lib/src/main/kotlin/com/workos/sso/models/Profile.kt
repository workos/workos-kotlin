package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.LinkedHashMap

data class Profile
@JsonCreator constructor(
    @JsonProperty("object")
    val obj: String,

    val id: String,

    @JsonProperty("idp_id")
    val idpId: String,

    @JsonProperty("organization_id")
    val organizationId: String,

    @JsonProperty("connection_id")
    val connectionId: String,

    @JsonProperty("connection_type")
    val connectionType: ConnectionType,

    val email: String,

    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String,

    @JsonProperty("raw_attributes")
    val rawAttributes: LinkedHashMap<String, Any>,
)
