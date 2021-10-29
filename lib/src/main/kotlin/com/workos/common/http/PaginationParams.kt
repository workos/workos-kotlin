package com.workos.common.http

import java.security.InvalidParameterException

interface BuilderParamValidation {
  fun validateBuilderParams(): Boolean
}

open class PaginationParams @JvmOverloads constructor(
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
    @JvmStatic
    fun builder(): Builder<PaginationParams> {
      return Builder(PaginationParams())
    }
  }

  open class Builder<T : PaginationParams>(protected val params: T): BuilderParamValidation {
    fun after(after: String) = apply { this.params["after"] = after }
    fun before(before: String) = apply { this.params["before"] = before }
    fun limit(limit: Int) = apply { this.params["limit"] = limit.toString() }
    open fun build(): T {
      if (!validateBuilderParams()) {
        throw InvalidParameterException("Invalid builder parameters")
      }
      return params
    }

    override fun validateBuilderParams(): Boolean {
      return true
    }
  }
}
