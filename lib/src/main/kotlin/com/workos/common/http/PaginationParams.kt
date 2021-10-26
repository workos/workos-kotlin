package com.workos.common.http

class PaginationParams private constructor(params: MutableMap<String, String>): HashMap<String, String>() {
    init {
        for ((key, value) in params) {
            this[key] = value
        }
    }

    data class Builder(private val params: MutableMap<String, String> = mutableMapOf()) {
        fun after(after: String) = apply { this.params["after"] = after }
        fun before(before: String) = apply { this.params["before"] = before }
        fun limit(limit: Int) = apply { this.params["limit"] = limit.toString() }
        fun build() = PaginationParams(params)
    }
}
