package com.workos.common.http

class PaginationParams private constructor() : HashMap<String, String>() {
    data class Builder(private var params: PaginationParams = PaginationParams()) {
        fun after(after: String) = apply { this.params["after"] = after }
        fun before(before: String) = apply { this.params["before"] = before }
        fun limit(limit: Int) = apply { this.params["limit"] = limit.toString() }
        fun build() = params
    }
}
