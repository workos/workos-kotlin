package com.workos.test.authorization

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.workos.authorization.builders.CreateAuthorizationResourceOptionsBuilder
import com.workos.authorization.builders.ListAuthorizationResourcesOptionsBuilder
import com.workos.authorization.builders.UpdateAuthorizationResourceOptionsBuilder
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthorizationApiTest : TestBase() {
  val workos = createWorkOSClient()

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
    assertNull(resource.parentResourceId)
  }

  @Test
  fun getResourceWithoutParentShouldReturnResourceWithNullParent() {
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
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }"""
    )

    val resource = workos.authorization.getResource("resource_01H")

    assertEquals("resource_01H", resource.id)
    assertEquals("A test document", resource.description)
    assertNull(resource.parentResourceId)
  }

  @Test
  fun getResourceWithoutDescriptionShouldReturnResourceWithNullDescription() {
    stubResponse(
      "/authorization/resources/resource_01H",
      """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "My Document",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "parent_resource_id": "resource_parent",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }"""
    )

    val resource = workos.authorization.getResource("resource_01H")

    assertEquals("resource_01H", resource.id)
    assertNull(resource.description)
    assertEquals("resource_parent", resource.parentResourceId)
  }

  @Test
  fun getResourceWithoutParentAndDescriptionShouldReturnResourceWithNulls() {
    stubResponse(
      "/authorization/resources/resource_01H",
      """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "My Document",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }"""
    )

    val resource = workos.authorization.getResource("resource_01H")

    assertEquals("resource_01H", resource.id)
    assertNull(resource.description)
    assertNull(resource.parentResourceId)
  }

  @Test
  fun createResourceWithParentAndDescriptionShouldReturnResource() {
    stubResponse(
      "/authorization/resources",
      """{
        "object": "authorization_resource",
        "id": "resource_02H",
        "external_id": "child-1",
        "name": "Child Resource",
        "description": "A child resource",
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
        "description": "A child resource",
        "parent_resource_id": "resource_01H"
      }"""
    )

    val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "page", "child-1", "Child Resource")
      .description("A child resource")
      .parentResourceId("resource_01H")
      .build()
    val resource = workos.authorization.createResource(options)

    assertEquals("resource_02H", resource.id)
    assertEquals("child-1", resource.externalId)
    assertEquals("Child Resource", resource.name)
    assertEquals("A child resource", resource.description)
    assertEquals("page", resource.resourceTypeSlug)
    assertEquals("org_01H", resource.organizationId)
    assertEquals("resource_01H", resource.parentResourceId)
  }

  @Test
  fun createResourceWithParentByExternalIdAndDescriptionShouldReturnResource() {
    stubResponse(
      "/authorization/resources",
      """{
        "object": "authorization_resource",
        "id": "resource_03H",
        "external_id": "child-2",
        "name": "Child Resource 2",
        "description": "Another child resource",
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
        "description": "Another child resource",
        "parent_resource_external_id": "my-doc-1",
        "parent_resource_type_slug": "document"
      }"""
    )

    val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "page", "child-2", "Child Resource 2")
      .description("Another child resource")
      .parentResourceExternalId("my-doc-1")
      .parentResourceTypeSlug("document")
      .build()
    val resource = workos.authorization.createResource(options)

    assertEquals("resource_03H", resource.id)
    assertEquals("child-2", resource.externalId)
    assertEquals("Child Resource 2", resource.name)
    assertEquals("Another child resource", resource.description)
    assertEquals("resource_01H", resource.parentResourceId)
  }

  @Test
  fun createResourceWithParentWithoutDescriptionShouldReturnResource() {
    stubResponse(
      "/authorization/resources",
      """{
        "object": "authorization_resource",
        "id": "resource_04H",
        "external_id": "child-3",
        "name": "Child Resource 3",
        "resource_type_slug": "page",
        "organization_id": "org_01H",
        "parent_resource_id": "resource_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }""",
      requestBody = """{
        "organization_id": "org_01H",
        "resource_type_slug": "page",
        "external_id": "child-3",
        "name": "Child Resource 3",
        "parent_resource_id": "resource_01H"
      }"""
    )

    val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "page", "child-3", "Child Resource 3")
      .parentResourceId("resource_01H")
      .build()
    val resource = workos.authorization.createResource(options)

    assertEquals("resource_04H", resource.id)
    assertNull(resource.description)
    assertEquals("resource_01H", resource.parentResourceId)
  }

  @Test
  fun createResourceWithParentByExternalIdWithoutDescriptionShouldReturnResource() {
    stubResponse(
      "/authorization/resources",
      """{
        "object": "authorization_resource",
        "id": "resource_05H",
        "external_id": "child-4",
        "name": "Child Resource 4",
        "resource_type_slug": "page",
        "organization_id": "org_01H",
        "parent_resource_id": "resource_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z"
      }""",
      requestBody = """{
        "organization_id": "org_01H",
        "resource_type_slug": "page",
        "external_id": "child-4",
        "name": "Child Resource 4",
        "parent_resource_external_id": "my-doc-1",
        "parent_resource_type_slug": "document"
      }"""
    )

    val options = CreateAuthorizationResourceOptionsBuilder("org_01H", "page", "child-4", "Child Resource 4")
      .parentResourceExternalId("my-doc-1")
      .parentResourceTypeSlug("document")
      .build()
    val resource = workos.authorization.createResource(options)

    assertEquals("resource_05H", resource.id)
    assertNull(resource.description)
    assertEquals("resource_01H", resource.parentResourceId)
  }

  @Test
  fun createResourceWithBlankNameShouldThrow() {
    assertFailsWith<IllegalArgumentException> {
      CreateAuthorizationResourceOptionsBuilder("org_01H", "document", "doc-1", " ")
        .build()
    }
  }

  @Test
  fun createResourceWithBlankExternalIdShouldThrow() {
    assertFailsWith<IllegalArgumentException> {
      CreateAuthorizationResourceOptionsBuilder("org_01H", "document", " ", "My Document")
        .build()
    }
  }

  @Test
  fun createResourceWithBothParentIdAndParentExternalIdShouldThrow() {
    assertFailsWith<IllegalArgumentException> {
      CreateAuthorizationResourceOptionsBuilder("org_01H", "document", "doc-1", "My Document")
        .parentResourceId("resource_01H")
        .parentResourceExternalId("my-doc-1")
        .build()
    }
  }

  @Test
  fun createResourceWithParentExternalIdWithoutTypeSlugShouldThrow() {
    assertFailsWith<IllegalArgumentException> {
      CreateAuthorizationResourceOptionsBuilder("org_01H", "document", "doc-1", "My Document")
        .parentResourceExternalId("my-doc-1")
        .build()
    }
  }

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

  @Test
  fun updateResourceWithNameOmittedShouldReturnResource() {
    stubResponse(
      "/authorization/resources/resource_01H",
      """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "Original Name",
        "description": "Updated description",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-02T00:00:00Z"
      }""",
      requestBody = """{
        "description": "Updated description"
      }"""
    )

    val options = UpdateAuthorizationResourceOptionsBuilder()
      .description("Updated description")
      .build()
    val resource = workos.authorization.updateResource("resource_01H", options)

    assertEquals("Original Name", resource.name)
    assertEquals("Updated description", resource.description)
  }

  @Test
  fun updateResourceWithDescriptionOmittedShouldReturnResource() {
    stubResponse(
      "/authorization/resources/resource_01H",
      """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "Updated Name",
        "description": "Original description",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-02T00:00:00Z"
      }""",
      requestBody = """{
        "name": "Updated Name"
      }"""
    )

    val options = UpdateAuthorizationResourceOptionsBuilder()
      .name("Updated Name")
      .build()
    val resource = workos.authorization.updateResource("resource_01H", options)

    assertEquals("Updated Name", resource.name)
    assertEquals("Original description", resource.description)
  }

  @Test
  fun updateResourceWithDescriptionNullShouldRemoveDescription() {
    stubResponse(
      "/authorization/resources/resource_01H",
      """{
        "object": "authorization_resource",
        "id": "resource_01H",
        "external_id": "my-doc-1",
        "name": "My Document",
        "resource_type_slug": "document",
        "organization_id": "org_01H",
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-02T00:00:00Z"
      }"""
    )

    val options = UpdateAuthorizationResourceOptionsBuilder()
      .build()
    val resource = workos.authorization.updateResource("resource_01H", options)

    assertEquals("My Document", resource.name)
    assertNull(resource.description)
  }

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
      params = mapOf("cascade_delete" to equalTo("true"))
    )

    assertDoesNotThrow { workos.authorization.deleteResource("resource_01H", cascadeDelete = true) }
  }

  @Test
  fun deleteResourceWithCascadeFalseShouldNotPassParam() {
    stubResponse("/authorization/resources/resource_01H", "")

    assertDoesNotThrow { workos.authorization.deleteResource("resource_01H", cascadeDelete = false) }
  }

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
      }""",
      params = mapOf(
        "organization_id" to equalTo("org_01H"),
        "resource_type_slug" to equalTo("document"),
        "search" to equalTo("test"),
        "limit" to equalTo("10")
      )
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

  @Test
  fun listResourcesWithMultipleResourcesShouldReturnAll() {
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
          },
          {
            "object": "authorization_resource",
            "id": "resource_02H",
            "external_id": "my-doc-2",
            "name": "My Document 2",
            "description": "Second document",
            "resource_type_slug": "document",
            "organization_id": "org_01H",
            "created_at": "2024-01-02T00:00:00Z",
            "updated_at": "2024-01-02T00:00:00Z"
          }
        ],
        "list_metadata": {
          "after": "resource_02H",
          "before": "resource_01H"
        }
      }"""
    )

    val resources = workos.authorization.listResources()

    assertEquals(2, resources.data.size)
    assertEquals("resource_01H", resources.data[0].id)
    assertEquals("my-doc-1", resources.data[0].externalId)
    assertNull(resources.data[0].description)
    assertEquals("resource_02H", resources.data[1].id)
    assertEquals("my-doc-2", resources.data[1].externalId)
    assertEquals("Second document", resources.data[1].description)
    assertEquals("resource_02H", resources.listMetadata.after)
    assertEquals("resource_01H", resources.listMetadata.before)
  }

  @Test
  fun listResourcesWithNoResourcesShouldReturnEmptyList() {
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

    val resources = workos.authorization.listResources()

    assertEquals(0, resources.data.size)
    assertNull(resources.listMetadata.after)
    assertNull(resources.listMetadata.before)
  }

  @Test
  fun listResourcesWithAfterCursorShouldPassParam() {
    stubResponse(
      "/authorization/resources",
      """{
        "data": [
          {
            "object": "authorization_resource",
            "id": "resource_02H",
            "external_id": "my-doc-2",
            "name": "My Document 2",
            "resource_type_slug": "document",
            "organization_id": "org_01H",
            "created_at": "2024-01-02T00:00:00Z",
            "updated_at": "2024-01-02T00:00:00Z"
          }
        ],
        "list_metadata": {
          "after": "resource_02H",
          "before": null
        }
      }""",
      params = mapOf("after" to equalTo("resource_01H"))
    )

    val options = ListAuthorizationResourcesOptionsBuilder()
      .after("resource_01H")
      .build()
    val resources = workos.authorization.listResources(options)

    assertEquals(1, resources.data.size)
    assertEquals("resource_02H", resources.data[0].id)
  }

  @Test
  fun listResourcesWithBeforeCursorShouldPassParam() {
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
          "after": null,
          "before": "resource_01H"
        }
      }""",
      params = mapOf("before" to equalTo("resource_02H"))
    )

    val options = ListAuthorizationResourcesOptionsBuilder()
      .before("resource_02H")
      .build()
    val resources = workos.authorization.listResources(options)

    assertEquals(1, resources.data.size)
    assertEquals("resource_01H", resources.data[0].id)
  }

  @Test
  fun listResourcesWithParentResourceFilterShouldPassParam() {
    stubResponse(
      "/authorization/resources",
      """{
        "data": [
          {
            "object": "authorization_resource",
            "id": "resource_02H",
            "external_id": "child-1",
            "name": "Child Resource",
            "resource_type_slug": "page",
            "organization_id": "org_01H",
            "parent_resource_id": "resource_01H",
            "created_at": "2024-01-01T00:00:00Z",
            "updated_at": "2024-01-01T00:00:00Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }""",
      params = mapOf("parent_resource_id" to equalTo("resource_01H"))
    )

    val options = ListAuthorizationResourcesOptionsBuilder()
      .parentResourceId("resource_01H")
      .build()
    val resources = workos.authorization.listResources(options)

    assertEquals(1, resources.data.size)
    assertEquals("resource_02H", resources.data[0].id)
    assertEquals("resource_01H", resources.data[0].parentResourceId)
  }

  @Test
  fun listResourcesWithParentExternalIdFilterShouldPassParam() {
    stubResponse(
      "/authorization/resources",
      """{
        "data": [
          {
            "object": "authorization_resource",
            "id": "resource_02H",
            "external_id": "child-1",
            "name": "Child Resource",
            "resource_type_slug": "page",
            "organization_id": "org_01H",
            "parent_resource_id": "resource_01H",
            "created_at": "2024-01-01T00:00:00Z",
            "updated_at": "2024-01-01T00:00:00Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": null
        }
      }""",
      params = mapOf(
        "parent_external_id" to equalTo("my-doc-1"),
        "parent_resource_type_slug" to equalTo("document")
      )
    )

    val options = ListAuthorizationResourcesOptionsBuilder()
      .parentExternalId("my-doc-1")
      .parentResourceTypeSlug("document")
      .build()
    val resources = workos.authorization.listResources(options)

    assertEquals(1, resources.data.size)
    assertEquals("resource_02H", resources.data[0].id)
    assertEquals("resource_01H", resources.data[0].parentResourceId)
  }

  @Test
  fun listResourcesWithSearchFilterShouldPassParam() {
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
          "after": null,
          "before": null
        }
      }""",
      params = mapOf("search" to equalTo("My Document"))
    )

    val options = ListAuthorizationResourcesOptionsBuilder()
      .search("My Document")
      .build()
    val resources = workos.authorization.listResources(options)

    assertEquals(1, resources.data.size)
    assertEquals("resource_01H", resources.data[0].id)
    assertEquals("My Document", resources.data[0].name)
  }
}
