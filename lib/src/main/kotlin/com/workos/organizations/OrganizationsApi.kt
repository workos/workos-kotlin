package com.workos.organizations

import com.workos.WorkOS
import com.workos.organizations.models.Organization

class OrganizationsApi(val workos: WorkOS) {
  fun deleteOrganization(id: String) {
    workos.delete("/organizations/$id")
  }

  fun getOrganization(id: String): Organization {
    return workos.get("/organizations/$id", Organization::class.java)
  }
}
