package com.workos.sso.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ConnectionDomain
@JsonCreator constructor(
    val domain: String,

    val id: String,
)

data class Connection
@JsonCreator constructor(
    @JsonProperty("connection_type")
    val connectionType: ConnectionType,

    @JsonProperty("created_at")
    val createdAt: String,

    val domains: List<ConnectionDomain>,

    val id: String,

    val name: String,

    @JsonProperty("organization_id")
    val organizationId: String,

    val state: String,

    @JsonProperty("updated_at")
    val updatedAt: String,
)

enum class ConnectionType(val type: String) {
    ADFSSAML("ADFSSAML"),
    AzureSAML("AzureSAML"),
    GenericOIDC("GenericOIDC"),
    GenericSAML("GenericSAML"),
    GoogleOAuth("GoogleOAuth"),
    GoogleSAML("GoogleSAML"),
    MagicLink("MagicLink"),
    OktaSAML("OktaSAML"),
    OneLoginSAML("OneLoginSAML"),
    PingFederateSAML("PingFederateSAML"),
    PingOneSAML("PingOneSAML"),
    VMwareSAML("VMwareSAML"),
}
