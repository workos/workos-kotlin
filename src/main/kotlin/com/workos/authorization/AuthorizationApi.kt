package com.workos.authorization

import com.workos.WorkOS
import com.workos.authorization.models.AuthorizationCheckResult
import com.workos.authorization.models.AuthorizationOrganizationMembershipList
import com.workos.authorization.models.AuthorizationResource
import com.workos.authorization.models.AuthorizationResourceList
import com.workos.authorization.models.RoleAssignment
import com.workos.authorization.models.RoleAssignmentList
import com.workos.authorization.types.AssignRoleOptions
import com.workos.authorization.types.CheckAuthorizationOptions
import com.workos.authorization.types.CreateAuthorizationResourceOptions
import com.workos.authorization.types.ListAuthorizationResourcesOptions
import com.workos.authorization.types.ListMembershipsForResourceByExternalIdOptions
import com.workos.authorization.types.ListMembershipsForResourceOptions
import com.workos.authorization.types.ListResourcesForMembershipOptions
import com.workos.authorization.types.ListRoleAssignmentsOptions
import com.workos.authorization.types.RemoveRoleOptions
import com.workos.authorization.types.UpdateAuthorizationResourceOptions
import com.workos.common.http.RequestConfig

class AuthorizationApi(private val workos: WorkOS) {

  /** Get a resource by internal ID. */
  fun getResource(resourceId: String): AuthorizationResource {
    return workos.get(
      "/authorization/resources/$resourceId",
      AuthorizationResource::class.java
    )
  }

  /** Create a new authorization resource. */
  fun createResource(options: CreateAuthorizationResourceOptions): AuthorizationResource {
    return workos.post(
      "/authorization/resources",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Update a resource by internal ID. */
  fun updateResource(resourceId: String, options: UpdateAuthorizationResourceOptions): AuthorizationResource {
    return workos.patch(
      "/authorization/resources/$resourceId",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a resource by internal ID. */
  fun deleteResource(resourceId: String, cascadeDelete: Boolean = false) {
    val config = if (cascadeDelete) {
      RequestConfig.builder().params(mapOf("cascade_delete" to "true")).build()
    } else {
      null
    }
    workos.delete("/authorization/resources/$resourceId", config)
  }

  /** List authorization resources. */
  fun listResources(options: ListAuthorizationResourcesOptions? = null): AuthorizationResourceList {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListAuthorizationResourcesOptions()) as Map<String, String>

    return workos.get(
      "/authorization/resources",
      AuthorizationResourceList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** Get a resource by external ID. */
  fun getResourceByExternalId(organizationId: String, resourceTypeSlug: String, externalId: String): AuthorizationResource {
    return workos.get(
      "/authorization/organizations/$organizationId/resources/$resourceTypeSlug/$externalId",
      AuthorizationResource::class.java
    )
  }

  /** Update a resource by external ID. */
  fun updateResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    options: UpdateAuthorizationResourceOptions
  ): AuthorizationResource {
    return workos.patch(
      "/authorization/organizations/$organizationId/resources/$resourceTypeSlug/$externalId",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a resource by external ID. */
  fun deleteResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    cascadeDelete: Boolean = false
  ) {
    val config = if (cascadeDelete) {
      RequestConfig.builder().params(mapOf("cascade_delete" to "true")).build()
    } else {
      null
    }
    workos.delete("/authorization/organizations/$organizationId/resources/$resourceTypeSlug/$externalId", config)
  }

  /** Check authorization for an organization membership. */
  fun check(organizationMembershipId: String, options: CheckAuthorizationOptions): AuthorizationCheckResult {
    return workos.post(
      "/authorization/organization_memberships/$organizationMembershipId/check",
      AuthorizationCheckResult::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** List role assignments for an organization membership. */
  fun listRoleAssignments(
    organizationMembershipId: String,
    options: ListRoleAssignmentsOptions? = null
  ): RoleAssignmentList {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListRoleAssignmentsOptions()) as Map<String, String>

    return workos.get(
      "/authorization/organization_memberships/$organizationMembershipId/role_assignments",
      RoleAssignmentList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** Assign a role to an organization membership. */
  fun assignRole(organizationMembershipId: String, options: AssignRoleOptions): RoleAssignment {
    return workos.post(
      "/authorization/organization_memberships/$organizationMembershipId/role_assignments",
      RoleAssignment::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Remove a role from an organization membership. */
  fun removeRole(organizationMembershipId: String, options: RemoveRoleOptions) {
    workos.deleteWithBody(
      "/authorization/organization_memberships/$organizationMembershipId/role_assignments",
      RequestConfig.builder().data(options).build()
    )
  }

  /** Remove a role assignment by ID. */
  fun removeRoleAssignment(organizationMembershipId: String, roleAssignmentId: String) {
    workos.delete(
      "/authorization/organization_memberships/$organizationMembershipId/role_assignments/$roleAssignmentId"
    )
  }

  /** List resources accessible to an organization membership. */
  fun listResourcesForMembership(
    organizationMembershipId: String,
    options: ListResourcesForMembershipOptions
  ): AuthorizationResourceList {
    val params: Map<String, String> =
      RequestConfig.toMap(options) as Map<String, String>

    return workos.get(
      "/authorization/organization_memberships/$organizationMembershipId/resources",
      AuthorizationResourceList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** List organization memberships that have access to a resource by internal ID. */
  fun listMembershipsForResource(
    resourceId: String,
    options: ListMembershipsForResourceOptions
  ): AuthorizationOrganizationMembershipList {
    val params: Map<String, String> =
      RequestConfig.toMap(options) as Map<String, String>

    return workos.get(
      "/authorization/resources/$resourceId/organization_memberships",
      AuthorizationOrganizationMembershipList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** List organization memberships that have access to a resource by external ID. */
  fun listMembershipsForResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    options: ListMembershipsForResourceByExternalIdOptions
  ): AuthorizationOrganizationMembershipList {
    val params: Map<String, String> =
      RequestConfig.toMap(options) as Map<String, String>

    return workos.get(
      "/authorization/organizations/$organizationId/resources/$resourceTypeSlug/$externalId/organization_memberships",
      AuthorizationOrganizationMembershipList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }
}
