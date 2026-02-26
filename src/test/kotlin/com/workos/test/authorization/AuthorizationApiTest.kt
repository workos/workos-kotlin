package com.workos.test.authorization

import com.workos.authorization.AuthorizationApi
import com.workos.authorization.builders.CheckAuthorizationOptionsBuilder
import com.workos.authorization.builders.CreateAuthorizationResourceOptionsBuilder
import com.workos.authorization.builders.ListAuthorizationResourcesOptionsBuilder
import com.workos.authorization.builders.UpdateAuthorizationResourceOptionsBuilder
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthorizationApiTest : TestBase() {
  val workos = createWorkOSClient()

  @Nested
  inner class GetResource {

    @Test
    fun getResourcePathShouldReturnCorrectPath() {
      assertEquals("/authorization/resources", AuthorizationApi.RESOURCES_PATH)
    }

    @Test
    fun getResourceShouldReturnResource() {
      stubResponse(
        "/authorization/resources/resource_01H",
        """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "My Document",
        "description": "A test document",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "parent_resource_id": null,
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }"""
      )

      val resource = workos.authorization.getResource("resource_01H")

      assertEquals("resource_01H", resource.id)
      assertEquals("my-doc-1", resource.externalId)
      assertEquals("My Document", resource.name)
      assertEquals("A test document", resource.description)
      assertEquals("document", resource.resourceTypeSlug)
      assertEquals("org_01H", resource.organizationId)
    }
  }

  @Nested
  inner class CreateResource {
    @Test
    fun createResourceShouldReturnResource() {
      stubResponse(
        "/authorization/resources",
        """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "My Document",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }""",
        requestBody = """{
        "organization_id": "org_01H",
        "resource_type_slug": "document",
        "external_id": "my-doc-1",
        "name": "My Document"
      }"""
      )

      val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "document", "my-doc-1", "My Document").build()
      val resource = workos.authorization.createResource(options)

      assertEquals("resource_01H", resource.id)
      assertEquals("my-doc-1", resource.externalId)
      assertEquals("document", resource.resourceTypeSlug)
    }

    @Test
    fun createResourceWithParentShouldReturnResource() {
      stubResponse(
        "/authorization/resources",
        """{
        "object": "authorization_resource",
        "id": "resource_02H",
        "external_id": "child-1",
        "name": "Child Resource",
        "resource_type_slug": "page",
        "organization_id": "org_01H",
        "parent_resource_id": "resource_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }""",
        requestBody = """{
        "organization_id": "org_01H",
        "resource_type_slug": "page",
        "external_id": "child-1",
        "name": "Child Resource",
        "parent_resource_id": "resource_01H"
      }"""
      )

      val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "page", "child-1", "Child Resource")
        .parentResourceId("resource_01H")
        .build()
      val resource = workos.authorization.createResource(options)

      assertEquals("resource_02H", resource.id)
      assertEquals("resource_01H", resource.parentResourceId)
    }

    @Test
    fun createResourceWithParentByExternalIdShouldReturnResource() {
      stubResponse(
        "/authorization/resources",
        """{
        "object": "authorization_resource",
        "id": "resource_03H",
        "external_id": "child-2",
        "name": "Child Resource 2",
        "resource_type_slug": "page",
        "organization_id": "org_01H",
        "parent_resource_id": "resource_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }""",
        requestBody = """{
        "organization_id": "org_01H",
        "resource_type_slug": "page",
        "external_id": "child-2",
        "name": "Child Resource 2",
        "parent_resource_external_id": "my-doc-1",
        "parent_resource_type_slug": "document"
      }"""
      )

      val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "page", "child-2", "Child Resource 2")
        .parentResourceExternalId("my-doc-1")
        .parentResourceTypeSlug("document")
        .build()
      val resource = workos.authorization.createResource(options)

      assertEquals("resource_03H", resource.id)
      assertEquals("resource_01H", resource.parentResourceId)
    }
  }

  @Nested
  inner class UpdateResource {
    @Test
    fun updateResourceShouldReturnResource() {
      stubResponse(
        "/authorization/resources/resource_01H",
        """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "Updated Name",
        "description": "Updated description",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-02T00:00:00Z"
      }""",
        requestBody = """{
        "name": "Updated Name",
        "description": "Updated description"
      }"""
      )

      val options = UpdateAuthorizationResourceOptionsBuilder()
        .name("Updated Name")
        .description("Updated description")
        .build()
      val resource = workos.authorization.updateResource("resource_01H", options)

      assertEquals("Updated Name", resource.name)
      assertEquals("Updated description", resource.description)
    }
  }

  @Nested
  inner class DeleteResource {
    @Test
    fun deleteResourceShouldSucceed() {
      stubResponse("/authorization/resources/resource_01H", "")

      assertDoesNotThrow { workos.authorization.deleteResource("resource_01H") }
    }

    @Test
    fun deleteResourceWithCascadeShouldPassParam() {
      stubResponse(
        "/authorization/resources/resource_01H",
        "",
        params = mapOf("cascade_delete" to com.github.tomakehurst.wiremock.client.WireMock.equalTo("true"))
      )

      assertDoesNotThrow { workos.authorization.deleteResource("resource_01H", cascadeDelete = true) }
    }
  }
  @Nested
  inner class ListResources {
    @Test
    fun listResourcesShouldReturnList() {
      stubResponse(
        "/authorization/resources",
        """{
        "data": [
          {
            "object": "authorization_resource",
            "id": "resource_01H",
            "external_id": "my-doc-1",
            "name": "My Document",
            "resource_type_slug": "document",
            "organization_id": "org_01H",
            "created_at": "2024-01-01T00:00:00Z",
            "updated_at": "2024-01-01T00:00:00Z"
          }
        ],
        "list_metadata": {
          "after": "resource_01H",
          "before": null
        }
      }"""
      )

      val resources = workos.authorization.listResources()

      assertEquals(1, resources.data.size)
      assertEquals("resource_01H", resources.data[0].id)
      assertEquals("resource_01H", resources.listMetadata.after)
    }

    @Test
    fun listResourcesWithFiltersShouldPassParams() {
      stubResponse(
        "/authorization/resources",
        """{
        "data": [],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }"""
      )

      val options = ListAuthorizationResourcesOptionsBuilder()
        .organizationId("org_01H")
        .resourceTypeSlug("document")
        .search("test")
        .limit(10)
        .build()
      val resources = workos.authorization.listResources(options)

      assertNotNull(resources)
      assertEquals(0, resources.data.size)
    }
  }

  @Nested class Check {
    @Test
    fun checkWithResourceIdShouldReturnAuthorized() {
      stubResponse(
        "/authorization/organization_memberships/om_01H/check",
        """{
        "authorized": true
      }""",
        requestBody = """{
        "permission_slug": "document:read",
        "resource_id": "resource_01H"
      }"""
      )

      val options = CheckAuthorizationOptionsBuilder("document:read")
        .resourceId("resource_01H")
        .build()
      val result = workos.authorization.check("om_01H", options)

      assertTrue(result.authorized)
    }

    @Test
    fun checkWithExternalIdShouldReturnAuthorized() {
      stubResponse(
        "/authorization/organization_memberships/om_01H/check",
        """{
        "authorized": true
      }""",
        requestBody = """{
        "permission_slug": "document:read",
        "resource_external_id": "my-doc-1",
        "resource_type_slug": "document"
      }"""
      )

      val options = CheckAuthorizationOptionsBuilder("document:read")
        .resourceExternalId("my-doc-1")
        .resourceTypeSlug("document")
        .build()
      val result = workos.authorization.check("om_01H", options)

      assertTrue(result.authorized)
    }

    @Test
    fun checkShouldReturnUnauthorized() {
      stubResponse(
        "/authorization/organization_memberships/om_01H/check",
        """{
        "authorized": false
      }""",
        requestBody = """{
        "permission_slug": "document:delete",
        "resource_id": "resource_01H"
      }"""
      )

      val options = CheckAuthorizationOptionsBuilder("document:delete")
        .resourceId("resource_01H")
        .build()
      val result = workos.authorization.check("om_01H", options)

      assertFalse(result.authorized)
    }
  }
}
