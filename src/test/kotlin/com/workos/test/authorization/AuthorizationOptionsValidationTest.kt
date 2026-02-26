package com.workos.test.authorization

import com.workos.authorization.types.AssignRoleOptions
import com.workos.authorization.types.CheckAuthorizationOptions
import com.workos.authorization.types.CreateAuthorizationResourceOptions
import com.workos.authorization.types.ListAuthorizationResourcesOptions
import com.workos.authorization.types.ListMembershipsForResourceByExternalIdOptions
import com.workos.authorization.types.ListMembershipsForResourceOptions
import com.workos.authorization.types.ListResourcesForMembershipOptions
import com.workos.authorization.types.RemoveRoleOptions
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AuthorizationOptionsValidationTest {

  // -- AssignRoleOptions --

  @Test
  fun assignRoleOptionsRequiresNonBlankRoleSlug() {
    assertFailsWith<IllegalArgumentException>("roleSlug is required") {
      AssignRoleOptions(roleSlug = "")
    }
    assertFailsWith<IllegalArgumentException>("roleSlug is required") {
      AssignRoleOptions(roleSlug = "  ")
    }
  }

  @Test
  fun assignRoleOptionsRejectsBothResourceIdAndResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("Cannot specify both resourceId and resourceExternalId") {
      AssignRoleOptions(
        roleSlug = "admin",
        resourceId = "resource_01H",
        resourceExternalId = "ext-1",
        resourceTypeSlug = "document"
      )
    }
  }

  @Test
  fun assignRoleOptionsRequiresResourceTypeSlugWithResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("resourceTypeSlug is required when resourceExternalId is specified") {
      AssignRoleOptions(
        roleSlug = "admin",
        resourceExternalId = "ext-1"
      )
    }
  }

  @Test
  fun assignRoleOptionsAcceptsValidCombinations() {
    AssignRoleOptions(roleSlug = "admin")
    AssignRoleOptions(roleSlug = "admin", resourceId = "resource_01H")
    AssignRoleOptions(roleSlug = "admin", resourceExternalId = "ext-1", resourceTypeSlug = "document")
  }

  // -- RemoveRoleOptions --

  @Test
  fun removeRoleOptionsRequiresNonBlankRoleSlug() {
    assertFailsWith<IllegalArgumentException>("roleSlug is required") {
      RemoveRoleOptions(roleSlug = "")
    }
    assertFailsWith<IllegalArgumentException>("roleSlug is required") {
      RemoveRoleOptions(roleSlug = "  ")
    }
  }

  @Test
  fun removeRoleOptionsRejectsBothResourceIdAndResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("Cannot specify both resourceId and resourceExternalId") {
      RemoveRoleOptions(
        roleSlug = "admin",
        resourceId = "resource_01H",
        resourceExternalId = "ext-1",
        resourceTypeSlug = "document"
      )
    }
  }

  @Test
  fun removeRoleOptionsRequiresResourceTypeSlugWithResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("resourceTypeSlug is required when resourceExternalId is specified") {
      RemoveRoleOptions(
        roleSlug = "admin",
        resourceExternalId = "ext-1"
      )
    }
  }

  @Test
  fun removeRoleOptionsAcceptsValidCombinations() {
    RemoveRoleOptions(roleSlug = "admin")
    RemoveRoleOptions(roleSlug = "admin", resourceId = "resource_01H")
    RemoveRoleOptions(roleSlug = "admin", resourceExternalId = "ext-1", resourceTypeSlug = "document")
  }

  // -- CheckAuthorizationOptions --

  @Test
  fun checkAuthorizationOptionsRequiresNonBlankPermissionSlug() {
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      CheckAuthorizationOptions(permissionSlug = "")
    }
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      CheckAuthorizationOptions(permissionSlug = "  ")
    }
  }

  @Test
  fun checkAuthorizationOptionsRejectsBothResourceIdAndResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("Cannot specify both resourceId and resourceExternalId") {
      CheckAuthorizationOptions(
        permissionSlug = "read",
        resourceId = "resource_01H",
        resourceExternalId = "ext-1",
        resourceTypeSlug = "document"
      )
    }
  }

  @Test
  fun checkAuthorizationOptionsRequiresResourceTypeSlugWithResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("resourceTypeSlug is required when resourceExternalId is specified") {
      CheckAuthorizationOptions(
        permissionSlug = "read",
        resourceExternalId = "ext-1"
      )
    }
  }

  @Test
  fun checkAuthorizationOptionsAcceptsValidCombinations() {
    CheckAuthorizationOptions(permissionSlug = "read")
    CheckAuthorizationOptions(permissionSlug = "read", resourceId = "resource_01H")
    CheckAuthorizationOptions(permissionSlug = "read", resourceExternalId = "ext-1", resourceTypeSlug = "document")
  }

  // -- CreateAuthorizationResourceOptions --

  @Test
  fun createAuthorizationResourceOptionsRequiresNonBlankOrganizationId() {
    assertFailsWith<IllegalArgumentException>("organizationId is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "",
        resourceTypeSlug = "document",
        externalId = "ext-1",
        name = "My Document"
      )
    }
    assertFailsWith<IllegalArgumentException>("organizationId is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "  ",
        resourceTypeSlug = "document",
        externalId = "ext-1",
        name = "My Document"
      )
    }
  }

  @Test
  fun createAuthorizationResourceOptionsRequiresNonBlankResourceTypeSlug() {
    assertFailsWith<IllegalArgumentException>("resourceTypeSlug is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "",
        externalId = "ext-1",
        name = "My Document"
      )
    }
    assertFailsWith<IllegalArgumentException>("resourceTypeSlug is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "  ",
        externalId = "ext-1",
        name = "My Document"
      )
    }
  }

  @Test
  fun createAuthorizationResourceOptionsRequiresNonBlankExternalId() {
    assertFailsWith<IllegalArgumentException>("externalId is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "document",
        externalId = "",
        name = "My Document"
      )
    }
    assertFailsWith<IllegalArgumentException>("externalId is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "document",
        externalId = "  ",
        name = "My Document"
      )
    }
  }

  @Test
  fun createAuthorizationResourceOptionsRequiresNonBlankName() {
    assertFailsWith<IllegalArgumentException>("name is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "document",
        externalId = "ext-1",
        name = ""
      )
    }
    assertFailsWith<IllegalArgumentException>("name is required") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "document",
        externalId = "ext-1",
        name = "  "
      )
    }
  }

  @Test
  fun createAuthorizationResourceOptionsRejectsBothParentResourceIdAndParentResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("Cannot specify both parentResourceId and parentResourceExternalId") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "document",
        externalId = "ext-1",
        name = "My Document",
        parentResourceId = "resource_01H",
        parentResourceExternalId = "parent-ext-1",
        parentResourceTypeSlug = "folder"
      )
    }
  }

  @Test
  fun createAuthorizationResourceOptionsRequiresParentResourceTypeSlugWithParentResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("parentResourceTypeSlug is required when parentResourceExternalId is specified") {
      CreateAuthorizationResourceOptions(
        organizationId = "org_01H",
        resourceTypeSlug = "document",
        externalId = "ext-1",
        name = "My Document",
        parentResourceExternalId = "parent-ext-1"
      )
    }
  }

  @Test
  fun createAuthorizationResourceOptionsAcceptsValidCombinations() {
    CreateAuthorizationResourceOptions(
      organizationId = "org_01H",
      resourceTypeSlug = "document",
      externalId = "ext-1",
      name = "My Document"
    )
    CreateAuthorizationResourceOptions(
      organizationId = "org_01H",
      resourceTypeSlug = "document",
      externalId = "ext-1",
      name = "My Document",
      parentResourceId = "resource_01H"
    )
    CreateAuthorizationResourceOptions(
      organizationId = "org_01H",
      resourceTypeSlug = "document",
      externalId = "ext-1",
      name = "My Document",
      parentResourceExternalId = "parent-ext-1",
      parentResourceTypeSlug = "folder"
    )
  }

  // -- ListResourcesForMembershipOptions --

  @Test
  fun listResourcesForMembershipOptionsRequiresNonBlankPermissionSlug() {
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      ListResourcesForMembershipOptions(permissionSlug = "")
    }
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      ListResourcesForMembershipOptions(permissionSlug = "  ")
    }
  }

  @Test
  fun listResourcesForMembershipOptionsRejectsBothParentResourceIdAndParentResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("Cannot specify both parentResourceId and parentResourceExternalId") {
      ListResourcesForMembershipOptions(
        permissionSlug = "read",
        parentResourceId = "resource_01H",
        parentResourceExternalId = "parent-ext-1",
        parentResourceTypeSlug = "folder"
      )
    }
  }

  @Test
  fun listResourcesForMembershipOptionsRequiresParentResourceTypeSlugWithParentResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("parentResourceTypeSlug is required when parentResourceExternalId is specified") {
      ListResourcesForMembershipOptions(
        permissionSlug = "read",
        parentResourceExternalId = "parent-ext-1"
      )
    }
  }

  @Test
  fun listResourcesForMembershipOptionsAcceptsValidCombinations() {
    ListResourcesForMembershipOptions(permissionSlug = "read")
    ListResourcesForMembershipOptions(permissionSlug = "read", parentResourceId = "resource_01H")
    ListResourcesForMembershipOptions(
      permissionSlug = "read",
      parentResourceExternalId = "parent-ext-1",
      parentResourceTypeSlug = "folder"
    )
  }

  // -- ListAuthorizationResourcesOptions --

  @Test
  fun listAuthorizationResourcesOptionsRejectsBothParentResourceIdAndParentResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("Cannot specify both parentResourceId and parentResourceExternalId") {
      ListAuthorizationResourcesOptions(
        parentResourceId = "resource_01H",
        parentResourceExternalId = "parent-ext-1",
        parentResourceTypeSlug = "folder"
      )
    }
  }

  @Test
  fun listAuthorizationResourcesOptionsRequiresParentResourceTypeSlugWithParentResourceExternalId() {
    assertFailsWith<IllegalArgumentException>("parentResourceTypeSlug is required when parentResourceExternalId is specified") {
      ListAuthorizationResourcesOptions(
        parentResourceExternalId = "parent-ext-1"
      )
    }
  }

  @Test
  fun listAuthorizationResourcesOptionsAcceptsValidCombinations() {
    ListAuthorizationResourcesOptions()
    ListAuthorizationResourcesOptions(parentResourceId = "resource_01H")
    ListAuthorizationResourcesOptions(
      parentResourceExternalId = "parent-ext-1",
      parentResourceTypeSlug = "folder"
    )
  }

  // -- ListMembershipsForResourceOptions --

  @Test
  fun listMembershipsForResourceOptionsRequiresNonBlankPermissionSlug() {
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      ListMembershipsForResourceOptions(permissionSlug = "")
    }
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      ListMembershipsForResourceOptions(permissionSlug = "  ")
    }
  }

  @Test
  fun listMembershipsForResourceOptionsAcceptsValidPermissionSlug() {
    ListMembershipsForResourceOptions(permissionSlug = "read")
  }

  // -- ListMembershipsForResourceByExternalIdOptions --

  @Test
  fun listMembershipsForResourceByExternalIdOptionsRequiresNonBlankPermissionSlug() {
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      ListMembershipsForResourceByExternalIdOptions(permissionSlug = "")
    }
    assertFailsWith<IllegalArgumentException>("permissionSlug is required") {
      ListMembershipsForResourceByExternalIdOptions(permissionSlug = "  ")
    }
  }

  @Test
  fun listMembershipsForResourceByExternalIdOptionsAcceptsValidPermissionSlug() {
    ListMembershipsForResourceByExternalIdOptions(permissionSlug = "read")
  }
}
