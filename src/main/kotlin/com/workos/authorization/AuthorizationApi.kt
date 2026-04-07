package com.workos.authorization

import com.workos.WorkOS
import com.workos.authorization.models.AuthorizationCheck
import com.workos.authorization.models.AuthorizationOrganizationMembershipList
import com.workos.authorization.models.AuthorizationPermission
import com.workos.authorization.models.AuthorizationPermissionList
import com.workos.authorization.models.AuthorizationResource
import com.workos.authorization.models.AuthorizationResourceList
import com.workos.authorization.models.AuthorizationRole
import com.workos.authorization.models.AuthorizationRoleList
import com.workos.authorization.models.RoleAssignment
import com.workos.authorization.models.RoleAssignmentList
import com.workos.authorization.types.AddRolePermissionOptions
import com.workos.authorization.types.AssignRoleOptions
import com.workos.authorization.types.CheckAuthorizationOptions
import com.workos.authorization.types.CreateAuthorizationResourceOptions
import com.workos.authorization.types.CreateOrganizationRoleOptions
import com.workos.authorization.types.CreatePermissionOptions
import com.workos.authorization.types.CreateRoleOptions
import com.workos.authorization.types.ListAuthorizationResourcesOptions
import com.workos.authorization.types.ListMembershipResourcesOptions
import com.workos.authorization.types.ListPermissionsOptions
import com.workos.authorization.types.ListResourceMembershipsOptions
import com.workos.authorization.types.ListRoleAssignmentsOptions
import com.workos.authorization.types.RemoveRoleOptions
import com.workos.authorization.types.SetRolePermissionsOptions
import com.workos.authorization.types.UpdateAuthorizationResourceOptions
import com.workos.authorization.types.UpdateOrganizationRoleOptions
import com.workos.authorization.types.UpdatePermissionOptions
import com.workos.authorization.types.UpdateRoleOptions
import com.workos.common.http.RequestConfig

class AuthorizationApi(private val workos: WorkOS) {

  // ── Access Checks ──────────────────────────────────────────────────────

  /** Check if an organization membership has a permission on a resource. */
  fun check(organizationMembershipId: String, options: CheckAuthorizationOptions): AuthorizationCheck {
    return workos.post(
      "/authorization/organization_memberships/$organizationMembershipId/check",
      AuthorizationCheck::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** List resources where an organization membership has a specific permission. */
  fun listMembershipResources(
    organizationMembershipId: String,
    options: ListMembershipResourcesOptions
  ): AuthorizationResourceList {
    val params: Map<String, String> =
      RequestConfig.toMap(options) as Map<String, String>

    return workos.get(
      "/authorization/organization_memberships/$organizationMembershipId/resources",
      AuthorizationResourceList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** List organization memberships that have a specific permission on a resource. */
  fun listResourceMemberships(
    resourceId: String,
    options: ListResourceMembershipsOptions
  ): AuthorizationOrganizationMembershipList {
    val params: Map<String, String> =
      RequestConfig.toMap(options) as Map<String, String>

    return workos.get(
      "/authorization/resources/$resourceId/organization_memberships",
      AuthorizationOrganizationMembershipList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** List organization memberships for a resource identified by external ID. */
  fun listResourceMembershipsByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    options: ListResourceMembershipsOptions
  ): AuthorizationOrganizationMembershipList {
    val params: Map<String, String> =
      RequestConfig.toMap(options) as Map<String, String>

    return workos.get(
      "/authorization/organizations/$organizationId/resources/$resourceTypeSlug/$externalId/organization_memberships",
      AuthorizationOrganizationMembershipList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  // ── Role Assignments ───────────────────────────────────────────────────

  /** List all role assignments for an organization membership. */
  @JvmOverloads
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

  /** Assign a role to an organization membership on a specific resource. */
  fun assignRole(organizationMembershipId: String, options: AssignRoleOptions): RoleAssignment {
    return workos.post(
      "/authorization/organization_memberships/$organizationMembershipId/role_assignments",
      RoleAssignment::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Remove a role assignment by role slug and resource. */
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

  // ── Resources ──────────────────────────────────────────────────────────

  /** List authorization resources. */
  @JvmOverloads
  fun listResources(options: ListAuthorizationResourcesOptions? = null): AuthorizationResourceList {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListAuthorizationResourcesOptions()) as Map<String, String>

    return workos.get(
      "/authorization/resources",
      AuthorizationResourceList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** Create an authorization resource. */
  fun createResource(options: CreateAuthorizationResourceOptions): AuthorizationResource {
    return workos.post(
      "/authorization/resources",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Get an authorization resource by ID. */
  fun getResource(resourceId: String): AuthorizationResource {
    return workos.get(
      "/authorization/resources/$resourceId",
      AuthorizationResource::class.java
    )
  }

  /** Update an authorization resource by ID. */
  fun updateResource(resourceId: String, options: UpdateAuthorizationResourceOptions): AuthorizationResource {
    return workos.patch(
      "/authorization/resources/$resourceId",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete an authorization resource by ID. */
  @JvmOverloads
  fun deleteResource(resourceId: String, cascadeDelete: Boolean = false) {
    val params = mutableMapOf<String, String>()
    if (cascadeDelete) params["cascade_delete"] = "true"

    workos.delete(
      "/authorization/resources/$resourceId",
      RequestConfig.builder().params(params).build()
    )
  }

  /** Get a resource by external ID. */
  fun getResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String
  ): AuthorizationResource {
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
  @JvmOverloads
  fun deleteResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    cascadeDelete: Boolean = false
  ) {
    val params = mutableMapOf<String, String>()
    if (cascadeDelete) params["cascade_delete"] = "true"

    workos.delete(
      "/authorization/organizations/$organizationId/resources/$resourceTypeSlug/$externalId",
      RequestConfig.builder().params(params).build()
    )
  }

  // ── Permissions ────────────────────────────────────────────────────────

  /** List all permissions. */
  @JvmOverloads
  fun listPermissions(options: ListPermissionsOptions? = null): AuthorizationPermissionList {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListPermissionsOptions()) as Map<String, String>

    return workos.get(
      "/authorization/permissions",
      AuthorizationPermissionList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** Create a permission. */
  fun createPermission(options: CreatePermissionOptions): AuthorizationPermission {
    return workos.post(
      "/authorization/permissions",
      AuthorizationPermission::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Get a permission by slug. */
  fun getPermission(slug: String): AuthorizationPermission {
    return workos.get(
      "/authorization/permissions/$slug",
      AuthorizationPermission::class.java
    )
  }

  /** Update a permission by slug. */
  fun updatePermission(slug: String, options: UpdatePermissionOptions): AuthorizationPermission {
    return workos.patch(
      "/authorization/permissions/$slug",
      AuthorizationPermission::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a permission by slug. */
  fun deletePermission(slug: String) {
    workos.delete("/authorization/permissions/$slug")
  }

  // ── Environment Roles ──────────────────────────────────────────────────

  /** List environment roles. */
  fun listRoles(): AuthorizationRoleList {
    return workos.get(
      "/authorization/roles",
      AuthorizationRoleList::class.java
    )
  }

  /** Create an environment role. */
  fun createRole(options: CreateRoleOptions): AuthorizationRole {
    return workos.post(
      "/authorization/roles",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Get an environment role by slug. */
  fun getRole(slug: String): AuthorizationRole {
    return workos.get(
      "/authorization/roles/$slug",
      AuthorizationRole::class.java
    )
  }

  /** Update an environment role by slug. */
  fun updateRole(slug: String, options: UpdateRoleOptions): AuthorizationRole {
    return workos.patch(
      "/authorization/roles/$slug",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Set permissions for an environment role (replaces all existing permissions). */
  fun setRolePermissions(slug: String, options: SetRolePermissionsOptions): AuthorizationRole {
    return workos.put(
      "/authorization/roles/$slug/permissions",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Add a permission to an environment role. */
  fun addRolePermission(slug: String, options: AddRolePermissionOptions): AuthorizationRole {
    return workos.post(
      "/authorization/roles/$slug/permissions",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  // ── Organization Roles ─────────────────────────────────────────────────

  /** List roles for an organization. */
  fun listOrganizationRoles(organizationId: String): AuthorizationRoleList {
    return workos.get(
      "/authorization/organizations/$organizationId/roles",
      AuthorizationRoleList::class.java
    )
  }

  /** Create a custom role for an organization. */
  fun createOrganizationRole(organizationId: String, options: CreateOrganizationRoleOptions): AuthorizationRole {
    return workos.post(
      "/authorization/organizations/$organizationId/roles",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Get an organization role by slug. */
  fun getOrganizationRole(organizationId: String, slug: String): AuthorizationRole {
    return workos.get(
      "/authorization/organizations/$organizationId/roles/$slug",
      AuthorizationRole::class.java
    )
  }

  /** Update an organization role by slug. */
  fun updateOrganizationRole(
    organizationId: String,
    slug: String,
    options: UpdateOrganizationRoleOptions
  ): AuthorizationRole {
    return workos.patch(
      "/authorization/organizations/$organizationId/roles/$slug",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a custom organization role. */
  fun deleteOrganizationRole(organizationId: String, slug: String) {
    workos.delete("/authorization/organizations/$organizationId/roles/$slug")
  }

  /** Set permissions for an organization role (replaces all existing permissions). */
  fun setOrganizationRolePermissions(
    organizationId: String,
    slug: String,
    options: SetRolePermissionsOptions
  ): AuthorizationRole {
    return workos.put(
      "/authorization/organizations/$organizationId/roles/$slug/permissions",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Add a permission to an organization role. */
  fun addOrganizationRolePermission(
    organizationId: String,
    slug: String,
    options: AddRolePermissionOptions
  ): AuthorizationRole {
    return workos.post(
      "/authorization/organizations/$organizationId/roles/$slug/permissions",
      AuthorizationRole::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Remove a permission from an organization role. */
  fun removeOrganizationRolePermission(
    organizationId: String,
    slug: String,
    permissionSlug: String
  ) {
    workos.delete(
      "/authorization/organizations/$organizationId/roles/$slug/permissions/$permissionSlug"
    )
  }
}
