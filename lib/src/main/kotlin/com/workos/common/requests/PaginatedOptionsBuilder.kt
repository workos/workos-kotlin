package com.workos.common.options

import org.apache.http.client.utils.URIBuilder
import kotlin.collections.HashMap
import kotlin.collections.Map

class PaginatedRequestBuilder {
    private val map: HashMap<String, String> = HashMap()

    fun after(after: String) {
        map.set("after", after)
    }

    fun before(before: String) {
        map.set("before", before)
    }

    fun limit(value: Int) {
        map.set("before", value.toString())
    }

    fun options(): Map<String, String> {
        return map.toMap()
    }
}
