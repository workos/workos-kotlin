package com.workos.organizations

import com.workos.WorkOS
import com.workos.organizations.models.Organization

class OrganizationsApi(val workos: WorkOS) {
  fun getOrganization(id: String): Organization {
    return workos.get("/organizations/$id", Organization::class.java)
  }
}
