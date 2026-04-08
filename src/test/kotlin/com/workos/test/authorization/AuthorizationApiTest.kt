package com.workos.test.authorization

import com.workos.authorization.types.AddRolePermissionOptions
import com.workos.authorization.types.AssignRoleOptions
import com.workos.authorization.types.CheckAuthorizationOptions
import com.workos.authorization.types.CreateAuthorizationResourceOptions
import com.workos.authorization.types.CreateOrganizationRoleOptions
import com.workos.authorization.types.CreatePermissionOptions
import com.workos.authorization.types.CreateRoleOptions
import com.workos.authorization.types.ListOrganizationMembershipsForResourceOptions
import com.workos.authorization.types.ListResourcesForOrganizationMembershipOptions
import com.workos.authorization.types.RemoveRoleOptions
import com.workos.authorization.types.SetRolePermissionsOptions
import com.workos.authorization.types.UpdateAuthorizationResourceOptions
import com.workos.authorization.types.UpdateOrganizationRoleOptions
import com.workos.authorization.types.UpdatePermissionOptions
import com.workos.authorization.types.UpdateRoleOptions
import com.workos.test.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthorizationApiTest : TestBase() {
  val workos = createWorkOSClient()

  // ── Access Checks ──────────────────────────────────────────────────────

  @Test
  fun checkShouldReturnAuthorized() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/check",
      requestBody = """{
        "permission_slug": "documents:read",
        "resource_id": "authz_resource_01ABC"
      }""",
      responseBody = """{"authorized": true}"""
    )

    val result = workos.authorization.check(
      "om_01HXYZ",
      CheckAuthorizationOptions("documents:read", resourceId = "authz_resource_01ABC")
    )

    assertTrue(result.authorized)
  }

  @Test
  fun checkWithExternalIdShouldReturnAuthorized() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/check",
      requestBody = """{
        "permission_slug": "documents:write",
        "resource_external_id": "proj-456",
        "resource_type_slug": "project"
      }""",
      responseBody = """{"authorized": false}"""
    )

    val result = workos.authorization.check(
      "om_01HXYZ",
      CheckAuthorizationOptions(
        "documents:write",
        resourceExternalId = "proj-456",
        resourceTypeSlug = "project"
      )
    )

    assertEquals(false, result.authorized)
  }

  // ── Resources ──────────────────────────────────────────────────────────

  @Test
  fun listResourcesShouldReturnResourceList() {
    stubResponse(
      url = "/authorization/resources",
      responseBody = """{
        "data": [
          {
            "id": "authz_resource_01ABC",
            "object": "authorization_resource",
            "external_id": "proj-456",
            "name": "Website Redesign",
            "description": "A project",
            "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
            "resource_type_slug": "project",
            "parent_resource_id": null,
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
    )

    val result = workos.authorization.listResources()

    assertEquals(1, result.data.size)
    assertEquals("authz_resource_01ABC", result.data[0].id)
    assertEquals("proj-456", result.data[0].externalId)
    assertEquals("Website Redesign", result.data[0].name)
    assertEquals("project", result.data[0].resourceTypeSlug)
  }

  @Test
  fun createResourceShouldReturnResource() {
    stubResponse(
      url = "/authorization/resources",
      requestBody = """{
        "external_id": "proj-456",
        "name": "Website Redesign",
        "resource_type_slug": "project",
        "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT"
      }""",
      responseBody = """{
        "id": "authz_resource_01ABC",
        "object": "authorization_resource",
        "external_id": "proj-456",
        "name": "Website Redesign",
        "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
        "resource_type_slug": "project",
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val resource = workos.authorization.createResource(
      CreateAuthorizationResourceOptions(
        externalId = "proj-456",
        name = "Website Redesign",
        resourceTypeSlug = "project",
        organizationId = "org_01EHZNVPK3SFK441A1RGBFSHRT"
      )
    )

    assertEquals("authz_resource_01ABC", resource.id)
    assertEquals("proj-456", resource.externalId)
  }

  @Test
  fun getResourceShouldReturnResource() {
    stubResponse(
      url = "/authorization/resources/authz_resource_01ABC",
      responseBody = """{
        "id": "authz_resource_01ABC",
        "object": "authorization_resource",
        "external_id": "proj-456",
        "name": "Website Redesign",
        "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
        "resource_type_slug": "project",
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val resource = workos.authorization.getResource("authz_resource_01ABC")

    assertEquals("authz_resource_01ABC", resource.id)
    assertEquals("Website Redesign", resource.name)
  }

  @Test
  fun updateResourceShouldReturnUpdatedResource() {
    stubResponse(
      url = "/authorization/resources/authz_resource_01ABC",
      requestBody = """{
        "name": "Updated Name"
      }""",
      responseBody = """{
        "id": "authz_resource_01ABC",
        "object": "authorization_resource",
        "external_id": "proj-456",
        "name": "Updated Name",
        "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
        "resource_type_slug": "project",
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val resource = workos.authorization.updateResource(
      "authz_resource_01ABC",
      UpdateAuthorizationResourceOptions(name = "Updated Name")
    )

    assertEquals("Updated Name", resource.name)
  }

  @Test
  fun deleteResourceShouldNotError() {
    stubResponse(
      url = "/authorization/resources/authz_resource_01ABC",
      responseBody = "{}"
    )

    workos.authorization.deleteResource("authz_resource_01ABC")
  }

  @Test
  fun getResourceByExternalIdShouldReturnResource() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/resources/project/proj-456",
      responseBody = """{
        "id": "authz_resource_01ABC",
        "object": "authorization_resource",
        "external_id": "proj-456",
        "name": "Website Redesign",
        "organization_id": "org_01ABC",
        "resource_type_slug": "project",
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val resource = workos.authorization.getResourceByExternalId("org_01ABC", "project", "proj-456")

    assertEquals("authz_resource_01ABC", resource.id)
    assertEquals("proj-456", resource.externalId)
  }

  @Test
  fun updateResourceByExternalIdShouldReturnUpdatedResource() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/resources/project/proj-456",
      requestBody = """{
        "name": "New Name"
      }""",
      responseBody = """{
        "id": "authz_resource_01ABC",
        "object": "authorization_resource",
        "external_id": "proj-456",
        "name": "New Name",
        "organization_id": "org_01ABC",
        "resource_type_slug": "project",
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val resource = workos.authorization.updateResourceByExternalId(
      "org_01ABC", "project", "proj-456",
      UpdateAuthorizationResourceOptions(name = "New Name")
    )

    assertEquals("New Name", resource.name)
  }

  @Test
  fun deleteResourceByExternalIdShouldNotError() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/resources/project/proj-456",
      responseBody = "{}"
    )

    workos.authorization.deleteResourceByExternalId("org_01ABC", "project", "proj-456")
  }

  // ── Role Assignments ───────────────────────────────────────────────────

  @Test
  fun listRoleAssignmentsShouldReturnList() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/role_assignments",
      responseBody = """{
        "data": [
          {
            "id": "role_assignment_01ABC",
            "object": "role_assignment",
            "role": {"slug": "admin"},
            "resource": {
              "id": "authz_resource_01ABC",
              "external_id": "proj-456",
              "resource_type_slug": "project"
            },
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
    )

    val result = workos.authorization.listRoleAssignments("om_01HXYZ")

    assertEquals(1, result.data.size)
    assertEquals("role_assignment_01ABC", result.data[0].id)
    assertEquals("admin", result.data[0].role.slug)
    assertEquals("authz_resource_01ABC", result.data[0].resource?.id)
  }

  @Test
  fun assignRoleShouldReturnRoleAssignment() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/role_assignments",
      requestBody = """{
        "role_slug": "admin",
        "resource_id": "authz_resource_01ABC"
      }""",
      responseBody = """{
        "id": "role_assignment_01ABC",
        "object": "role_assignment",
        "role": {"slug": "admin"},
        "resource": {
          "id": "authz_resource_01ABC",
          "external_id": "proj-456",
          "resource_type_slug": "project"
        },
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val assignment = workos.authorization.assignRole(
      "om_01HXYZ",
      AssignRoleOptions("admin", resourceId = "authz_resource_01ABC")
    )

    assertEquals("role_assignment_01ABC", assignment.id)
    assertEquals("admin", assignment.role.slug)
  }

  @Test
  fun removeRoleShouldNotError() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/role_assignments",
      requestBody = """{
        "role_slug": "admin",
        "resource_id": "authz_resource_01ABC"
      }""",
      responseBody = "{}"
    )

    workos.authorization.removeRole(
      "om_01HXYZ",
      RemoveRoleOptions("admin", resourceId = "authz_resource_01ABC")
    )
  }

  @Test
  fun removeRoleAssignmentByIdShouldNotError() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/role_assignments/role_assignment_01ABC",
      responseBody = "{}"
    )

    workos.authorization.removeRoleAssignment("om_01HXYZ", "role_assignment_01ABC")
  }

  // ── Membership Resources ───────────────────────────────────────────────

  @Test
  fun listResourcesForOrganizationMembershipShouldReturnList() {
    stubResponse(
      url = "/authorization/organization_memberships/om_01HXYZ/resources",
      responseBody = """{
        "data": [
          {
            "id": "authz_resource_01ABC",
            "object": "authorization_resource",
            "external_id": "proj-456",
            "name": "Website Redesign",
            "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
            "resource_type_slug": "project",
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
    )

    val result = workos.authorization.listResourcesForOrganizationMembership(
      "om_01HXYZ",
      ListResourcesForOrganizationMembershipOptions("documents:read")
    )

    assertEquals(1, result.data.size)
    assertEquals("authz_resource_01ABC", result.data[0].id)
  }

  // ── Resource Memberships ───────────────────────────────────────────────

  @Test
  fun listOrganizationMembershipsForResourceShouldReturnList() {
    stubResponse(
      url = "/authorization/resources/authz_resource_01ABC/organization_memberships",
      responseBody = """{
        "data": [
          {
            "id": "om_01HXYZ",
            "object": "organization_membership",
            "user_id": "user_01EHQTV6MWP9P1F4ZXGXMC8ABB",
            "organization_id": "org_01EHZNVPK3SFK441A1RGBFSHRT",
            "status": "active",
            "directory_managed": false,
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
    )

    val result = workos.authorization.listOrganizationMembershipsForResource(
      "authz_resource_01ABC",
      ListOrganizationMembershipsForResourceOptions("documents:read")
    )

    assertEquals(1, result.data.size)
    assertEquals("om_01HXYZ", result.data[0].id)
    assertEquals("user_01EHQTV6MWP9P1F4ZXGXMC8ABB", result.data[0].userId)
    assertEquals("active", result.data[0].status.type)
  }

  @Test
  fun listOrganizationMembershipsForResourceByExternalIdShouldReturnList() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/resources/project/proj-456/organization_memberships",
      responseBody = """{
        "data": [
          {
            "id": "om_01HXYZ",
            "object": "organization_membership",
            "user_id": "user_01ABC",
            "organization_id": "org_01ABC",
            "status": "active",
            "directory_managed": false,
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
    )

    val result = workos.authorization.listOrganizationMembershipsForResourceByExternalId(
      "org_01ABC", "project", "proj-456",
      ListOrganizationMembershipsForResourceOptions("documents:read")
    )

    assertEquals(1, result.data.size)
    assertEquals("om_01HXYZ", result.data[0].id)
  }

  // ── Permissions ────────────────────────────────────────────────────────

  @Test
  fun listPermissionsShouldReturnList() {
    stubResponse(
      url = "/authorization/permissions",
      responseBody = """{
        "data": [
          {
            "id": "perm_01ABC",
            "object": "permission",
            "slug": "documents:read",
            "name": "Read Documents",
            "description": "Can read documents",
            "system": false,
            "resource_type_slug": "document",
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
    )

    val result = workos.authorization.listPermissions()

    assertEquals(1, result.data.size)
    assertEquals("documents:read", result.data[0].slug)
    assertEquals("Read Documents", result.data[0].name)
    assertEquals(false, result.data[0].system)
  }

  @Test
  fun createPermissionShouldReturnPermission() {
    stubResponse(
      url = "/authorization/permissions",
      requestBody = """{
        "slug": "documents:read",
        "name": "Read Documents"
      }""",
      responseBody = """{
        "id": "perm_01ABC",
        "object": "permission",
        "slug": "documents:read",
        "name": "Read Documents",
        "system": false,
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val permission = workos.authorization.createPermission(
      CreatePermissionOptions("documents:read", "Read Documents")
    )

    assertEquals("perm_01ABC", permission.id)
    assertEquals("documents:read", permission.slug)
  }

  @Test
  fun getPermissionShouldReturnPermission() {
    stubResponse(
      url = "/authorization/permissions/documents:read",
      responseBody = """{
        "id": "perm_01ABC",
        "object": "permission",
        "slug": "documents:read",
        "name": "Read Documents",
        "system": false,
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val permission = workos.authorization.getPermission("documents:read")

    assertEquals("perm_01ABC", permission.id)
  }

  @Test
  fun updatePermissionShouldReturnUpdatedPermission() {
    stubResponse(
      url = "/authorization/permissions/documents:read",
      requestBody = """{
        "name": "Read All Documents"
      }""",
      responseBody = """{
        "id": "perm_01ABC",
        "object": "permission",
        "slug": "documents:read",
        "name": "Read All Documents",
        "system": false,
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val permission = workos.authorization.updatePermission(
      "documents:read",
      UpdatePermissionOptions(name = "Read All Documents")
    )

    assertEquals("Read All Documents", permission.name)
  }

  @Test
  fun deletePermissionShouldNotError() {
    stubResponse(
      url = "/authorization/permissions/documents:read",
      responseBody = "{}"
    )

    workos.authorization.deletePermission("documents:read")
  }

  // ── Environment Roles ──────────────────────────────────────────────────

  @Test
  fun listRolesShouldReturnList() {
    stubResponse(
      url = "/authorization/roles",
      responseBody = """{
        "data": [
          {
            "id": "role_01ABC",
            "object": "role",
            "slug": "admin",
            "name": "Admin",
            "description": "Full access",
            "type": "EnvironmentRole",
            "permissions": ["documents:read", "documents:write"],
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ]
      }"""
    )

    val result = workos.authorization.listRoles()

    assertEquals(1, result.data.size)
    assertEquals("admin", result.data[0].slug)
    assertEquals("EnvironmentRole", result.data[0].type)
    assertEquals(listOf("documents:read", "documents:write"), result.data[0].permissions)
  }

  @Test
  fun createRoleShouldReturnRole() {
    stubResponse(
      url = "/authorization/roles",
      requestBody = """{
        "slug": "editor",
        "name": "Editor"
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "editor",
        "name": "Editor",
        "type": "EnvironmentRole",
        "permissions": [],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.createRole(
      CreateRoleOptions("editor", "Editor")
    )

    assertEquals("role_01ABC", role.id)
    assertEquals("editor", role.slug)
  }

  @Test
  fun getRoleShouldReturnRole() {
    stubResponse(
      url = "/authorization/roles/admin",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "admin",
        "name": "Admin",
        "type": "EnvironmentRole",
        "permissions": ["documents:read"],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.getRole("admin")

    assertEquals("admin", role.slug)
  }

  @Test
  fun updateRoleShouldReturnUpdatedRole() {
    stubResponse(
      url = "/authorization/roles/admin",
      requestBody = """{
        "name": "Super Admin"
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "admin",
        "name": "Super Admin",
        "type": "EnvironmentRole",
        "permissions": [],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.updateRole(
      "admin",
      UpdateRoleOptions(name = "Super Admin")
    )

    assertEquals("Super Admin", role.name)
  }

  @Test
  fun setRolePermissionsShouldReturnRole() {
    stubResponse(
      url = "/authorization/roles/admin/permissions",
      requestBody = """{
        "permissions": ["documents:read", "documents:write"]
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "admin",
        "name": "Admin",
        "type": "EnvironmentRole",
        "permissions": ["documents:read", "documents:write"],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.setRolePermissions(
      "admin",
      SetRolePermissionsOptions(listOf("documents:read", "documents:write"))
    )

    assertEquals(listOf("documents:read", "documents:write"), role.permissions)
  }

  @Test
  fun addRolePermissionShouldReturnRole() {
    stubResponse(
      url = "/authorization/roles/admin/permissions",
      requestBody = """{
        "slug": "documents:delete"
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "admin",
        "name": "Admin",
        "type": "EnvironmentRole",
        "permissions": ["documents:read", "documents:delete"],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.addRolePermission(
      "admin",
      AddRolePermissionOptions("documents:delete")
    )

    assertEquals(2, role.permissions?.size)
  }

  // ── Organization Roles ─────────────────────────────────────────────────

  @Test
  fun listOrganizationRolesShouldReturnList() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles",
      responseBody = """{
        "data": [
          {
            "id": "role_01ABC",
            "object": "role",
            "slug": "org-admin",
            "name": "Org Admin",
            "type": "OrganizationRole",
            "permissions": ["documents:read"],
            "created_at": "2026-01-15T12:00:00.000Z",
            "updated_at": "2026-01-15T12:00:00.000Z"
          }
        ]
      }"""
    )

    val result = workos.authorization.listOrganizationRoles("org_01ABC")

    assertEquals(1, result.data.size)
    assertEquals("org-admin", result.data[0].slug)
    assertEquals("OrganizationRole", result.data[0].type)
  }

  @Test
  fun createOrganizationRoleShouldReturnRole() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles",
      requestBody = """{
        "name": "Org Editor"
      }""",
      responseBody = """{
        "id": "role_01DEF",
        "object": "role",
        "slug": "org-editor",
        "name": "Org Editor",
        "type": "OrganizationRole",
        "permissions": [],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.createOrganizationRole(
      "org_01ABC",
      CreateOrganizationRoleOptions("Org Editor")
    )

    assertEquals("role_01DEF", role.id)
    assertEquals("org-editor", role.slug)
  }

  @Test
  fun getOrganizationRoleShouldReturnRole() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles/org-admin",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "org-admin",
        "name": "Org Admin",
        "type": "OrganizationRole",
        "permissions": ["documents:read"],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.getOrganizationRole("org_01ABC", "org-admin")

    assertEquals("org-admin", role.slug)
  }

  @Test
  fun updateOrganizationRoleShouldReturnUpdatedRole() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles/org-admin",
      requestBody = """{
        "name": "Organization Administrator"
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "org-admin",
        "name": "Organization Administrator",
        "type": "OrganizationRole",
        "permissions": [],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.updateOrganizationRole(
      "org_01ABC", "org-admin",
      UpdateOrganizationRoleOptions(name = "Organization Administrator")
    )

    assertEquals("Organization Administrator", role.name)
  }

  @Test
  fun deleteOrganizationRoleShouldNotError() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles/org-custom",
      responseBody = "{}"
    )

    workos.authorization.deleteOrganizationRole("org_01ABC", "org-custom")
  }

  @Test
  fun setOrganizationRolePermissionsShouldReturnRole() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles/org-admin/permissions",
      requestBody = """{
        "permissions": ["documents:read", "documents:write"]
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "org-admin",
        "name": "Org Admin",
        "type": "OrganizationRole",
        "permissions": ["documents:read", "documents:write"],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.setOrganizationRolePermissions(
      "org_01ABC", "org-admin",
      SetRolePermissionsOptions(listOf("documents:read", "documents:write"))
    )

    assertEquals(listOf("documents:read", "documents:write"), role.permissions)
  }

  @Test
  fun addOrganizationRolePermissionShouldReturnRole() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles/org-admin/permissions",
      requestBody = """{
        "slug": "documents:delete"
      }""",
      responseBody = """{
        "id": "role_01ABC",
        "object": "role",
        "slug": "org-admin",
        "name": "Org Admin",
        "type": "OrganizationRole",
        "permissions": ["documents:read", "documents:delete"],
        "created_at": "2026-01-15T12:00:00.000Z",
        "updated_at": "2026-01-15T12:00:00.000Z"
      }"""
    )

    val role = workos.authorization.addOrganizationRolePermission(
      "org_01ABC", "org-admin",
      AddRolePermissionOptions("documents:delete")
    )

    assertEquals(2, role.permissions?.size)
  }

  @Test
  fun removeOrganizationRolePermissionShouldNotError() {
    stubResponse(
      url = "/authorization/organizations/org_01ABC/roles/org-admin/permissions/documents:delete",
      responseBody = "{}"
    )

    workos.authorization.removeOrganizationRolePermission("org_01ABC", "org-admin", "documents:delete")
  }
}
