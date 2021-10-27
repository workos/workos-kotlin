package com.workos.common.http

class PaginationParams @JvmOverloads constructor(
    after: String? = null,
    before: String? = null,
    limit: Int? = null
) : HashMap<String, String>() {

    init {
        if (after != null) {
            set("after", after)
        }

        if (before != null) {
            set("before", before)
        }

        if (limit != null) {
            set("limit", limit.toString())
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder(private var params: PaginationParams = PaginationParams()) {
        fun after(after: String) = apply { this.params["after"] = after }
        fun before(before: String) = apply { this.params["before"] = before }
        fun limit(limit: Int) = apply { this.params["limit"] = limit.toString() }
        fun build() = params
    }
}
