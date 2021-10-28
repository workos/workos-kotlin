package com.workos.organizations

import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.organizations.models.Organization
import com.workos.organizations.models.OrganizationList

class OrganizationsApi(val workos: WorkOS) {
  fun deleteOrganization(id: String) {
    workos.delete("/organizations/$id")
  }

  fun getOrganization(id: String): Organization {
    return workos.get("/organizations/$id", Organization::class.java)
  }

  class ListOrganizationsOptions @JvmOverloads constructor(
    domains: List<String>? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null
  ) : PaginationParams(after, before, limit) {
    init {
      if (domains != null) set("domains", domains.joinToString(","))
    }

    companion object {
      fun builder(): Builder {
        return Builder()
      }
    }

    class Builder : PaginationParams.Builder<ListOrganizationsOptions>(ListOrganizationsOptions()) {
      fun domains(value: List<String>) = apply { this.params["domains"] = value.joinToString(",") }
    }
  }

  fun listOrganizations(options: ListOrganizationsOptions = ListOrganizationsOptions()): OrganizationList {
    val config = RequestConfig.builder()
      .params(options)
      .build()

    return workos.get("/organizations", OrganizationList::class.java, config)
  }
}
