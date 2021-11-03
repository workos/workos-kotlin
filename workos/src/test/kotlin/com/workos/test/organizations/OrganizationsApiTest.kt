package com.workos.test.organizations

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.exceptions.UnauthorizedException
import com.workos.organizations.OrganizationsApi
import com.workos.organizations.OrganizationsApi.CreateOrganizationOptions
import com.workos.organizations.OrganizationsApi.UpdateOrganizationOptions
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class OrganizationsApiTest : TestBase() {
  fun prepareCreateOrganizationTest(body: String): Map<String, String> {
    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"

    stubResponse(
      url = "/organizations",
      responseBody = """{
        "name": "$organizationDomainName",
        "object": "organization",
        "id": "$organizationId",
        "allow_profiles_outside_organization": false,
        "created_at": "2021-10-28T15:13:51.874Z",
        "updated_at": "2021-10-28T15:14:03.032Z",
        "domains": [
          {
            "domain": "example.com",
            "object": "organization_domain",
            "id": "$organizationDomainId"
          }
        ]
      }""",
      requestBody = body,
    )

    return mapOf(
      "organizationId" to organizationId,
      "organizationDomainId" to organizationDomainId,
      "organizationDomainName" to organizationDomainName
    )
  }

  @Test
  fun createOrganizationWithDefaultsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareCreateOrganizationTest(
      """{}"""
    )

    val organization = workos.organizations.createOrganization()

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun createOrganizationWithOptionsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareCreateOrganizationTest(
      """{
        "name": "Organization Name",
        "allow_profiles_outside_organization": true,
        "domains": ["domain1.com", "domain2.com"]
      }"""
    )

    val config = CreateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(true)
      .domains(listOf("domain1.com", "domain2.com"))
      .build()

    val organization = workos.organizations.createOrganization(config)

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun createOrganizationWithRawOptionsReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareCreateOrganizationTest(
      """{
        "name": "Organization Name",
        "allow_profiles_outside_organization": false,
        "domains": ["foo.com", "bar.com"]
      }"""
    )

    val organization = workos.organizations.createOrganization(
      CreateOrganizationOptions(
        "Organization Name",
        false,
        listOf("foo.com", "bar.com")
      )
    )

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun deleteOrganizationShouldNotError() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"

    stubResponse(
      "/organizations/$organizationId",
      "{}"
    )

    val response = workos.organizations.deleteOrganization(organizationId)

    assertEquals(Unit, response)
  }

  @Test
  fun deleteOrganizationShouldThrowError() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"

    stubResponse(
      "/organizations/$organizationId",
      "{}",
      401
    )

    assertThrows(UnauthorizedException::class.java) {
      workos.organizations.deleteOrganization(organizationId)
    }
  }

  @Test
  fun getOrganizationShouldReturnPayload() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"

    stubResponse(
      url = "/organizations/$organizationId",
      responseBody = """{
        "name": "$organizationDomainName",
        "object": "organization",
        "id": "$organizationId",
        "allow_profiles_outside_organization": false,
        "created_at": "2021-10-28T15:13:51.874Z",
        "updated_at": "2021-10-28T15:14:03.032Z",
        "domains": [
          {
            "domain": "example.com",
            "object": "organization_domain",
            "id": "$organizationDomainId"
          }
        ]
      }"""
    )

    val organization = workos.organizations.getOrganization(organizationId)

    assertEquals(organizationId, organization.id)
    assertEquals(organizationDomainName, organization.name)
    assertEquals(organizationDomainId, organization.domains[0].id)
  }

  @Test
  fun listOrganizationsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"

    stubResponse(
      "/organizations",
      """{
        "data": [
          {
            "name": "$organizationDomainName",
            "object": "organization",
            "id": "$organizationId",
            "allow_profiles_outside_organization": false,
            "created_at": "2021-10-28T15:13:51.874Z",
            "updated_at": "2021-10-28T15:14:03.032Z",
            "domains": [
              {
                "domain": "example.com",
                "object": "organization_domain",
                "id": "$organizationDomainId"
              }
            ]
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "org_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
        }
      }"""
    )

    val (organizations) = workos.organizations.listOrganizations(OrganizationsApi.ListOrganizationsOptions.builder().build())

    assertEquals(organizationId, organizations.get(0).id)
    assertEquals(organizationDomainName, organizations.get(0).name)
    assertEquals(organizationDomainId, organizations.get(0).domains[0].id)
  }

  @Test
  fun listOrganizationsWithPaginationParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"

    stubResponse(
      url = "/organizations",
      params = mapOf("after" to equalTo("someAfterId"), "before" to equalTo("someBeforeId")),
      responseBody = """{
        "data": [
          {
            "name": "$organizationDomainName",
            "object": "organization",
            "id": "$organizationId",
            "allow_profiles_outside_organization": false,
            "created_at": "2021-10-28T15:13:51.874Z",
            "updated_at": "2021-10-28T15:14:03.032Z",
            "domains": [
              {
                "domain": "example.com",
                "object": "organization_domain",
                "id": "$organizationDomainId"
              }
            ]
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "org_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
        }
      }"""
    )

    val options = OrganizationsApi.ListOrganizationsOptions.builder()
      .after("someAfterId")
      .before("someBeforeId")
      .build()

    val (organizations) = workos.organizations.listOrganizations(options)

    assertEquals(organizationId, organizations.get(0).id)
  }

  @Test
  fun listOrganizationsWithOptionalParamsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"

    stubResponse(
      url = "/organizations",
      params = mapOf("domains" to equalTo("domain1.com,domain2.com")),
      responseBody = """{
        "data": [
          {
            "name": "$organizationDomainName",
            "object": "organization",
            "id": "$organizationId",
            "allow_profiles_outside_organization": false,
            "created_at": "2021-10-28T15:13:51.874Z",
            "updated_at": "2021-10-28T15:14:03.032Z",
            "domains": [
              {
                "domain": "example.com",
                "object": "organization_domain",
                "id": "$organizationDomainId"
              }
            ]
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "org_99FJYCNTBC2ZTKT4CS1BX0WJ2B"
        }
      }"""
    )

    val options = OrganizationsApi.ListOrganizationsOptions.builder()
      .domains(listOf("domain1.com", "domain2.com"))
      .build()

    val (organizations) = workos.organizations.listOrganizations(options)

    assertEquals(organizationId, organizations.get(0).id)
  }

  fun prepareUpdateOrganizationTest(body: String): Map<String, String> {
    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"

    stubResponse(
      url = "/organizations/$organizationId",
      responseBody = """{
        "name": "$organizationDomainName",
        "object": "organization",
        "id": "$organizationId",
        "allow_profiles_outside_organization": false,
        "created_at": "2021-10-28T15:13:51.874Z",
        "updated_at": "2021-10-28T15:14:03.032Z",
        "domains": [
          {
            "domain": "example.com",
            "object": "organization_domain",
            "id": "$organizationDomainId"
          }
        ]
      }""",
      requestBody = body,
    )

    return mapOf(
      "organizationId" to organizationId,
      "organizationDomainId" to organizationDomainId,
      "organizationDomainName" to organizationDomainName
    )
  }

  @Test
  fun updateOrganizationWithDefaultsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareUpdateOrganizationTest(
      """{}"""
    )

    val organization = workos.organizations.updateOrganization(data["organizationId"]!!)

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun updateOrganizationWithOptionsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareUpdateOrganizationTest(
      """{
        "name": "Organization Name",
        "allow_profiles_outside_organization": true,
        "domains": ["domain1.com", "domain2.com"]
      }"""
    )

    val config = UpdateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(true)
      .domains(listOf("domain1.com", "domain2.com"))
      .build()

    val organization = workos.organizations.updateOrganization(data["organizationId"]!!, config)

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun updateOrganizationWithPartialOptionsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareUpdateOrganizationTest(
      """{
        "name": "New Name"
      }"""
    )

    val config = UpdateOrganizationOptions.builder()
      .name("New Name")
      .build()

    val organization = workos.organizations.updateOrganization(data["organizationId"]!!, config)

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun updateOrganizationWithRawOptionsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareUpdateOrganizationTest(
      """{
        "name": "Organization Name",
        "allow_profiles_outside_organization": false,
        "domains": ["foo.com", "bar.com"]
      }"""
    )

    val organization = workos.organizations.updateOrganization(
      data["organizationId"]!!,
      UpdateOrganizationOptions(
        "Organization Name",
        false,
        listOf("foo.com", "bar.com")
      )
    )

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }
}
