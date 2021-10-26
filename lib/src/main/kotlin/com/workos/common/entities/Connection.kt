package com.workos.common.entities

interface ConnectionDomain {
    val domain: String
    val id: String
}

interface Connection {
    val connection_type: String
    val created_at: String
    val domains: List<ConnectionDomain>
    val id: String
    val name: String
    val organization_id: String
    val state: String
    val updated_at: String
}
