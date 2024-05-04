package com.workos.test.organizations

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.exceptions.UnauthorizedException
import com.workos.organizations.OrganizationsApi
import com.workos.organizations.OrganizationsApi.CreateOrganizationOptions
import com.workos.organizations.OrganizationsApi.CreateOrganizationRequestOptions
import com.workos.organizations.OrganizationsApi.OrganizationDomainDataOptions
import com.workos.organizations.OrganizationsApi.OrganizationDomainDataState
import com.workos.organizations.OrganizationsApi.UpdateOrganizationOptions
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class OrganizationsApiTest : TestBase() {
  private fun prepareCreateOrganizationTest(body: String): Map<String, String> {
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
      requestBody = body
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
        "domain_data": [
          {
            "domain": "example.com",
            "state": "pending"
          }
        ]
      }"""
    )

    val config = CreateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(true)
      .domainData(listOf(
        OrganizationDomainDataOptions(
          "example.com",
          OrganizationDomainDataState.PENDING
        )
      ))
      .build()

    val organization = workos.organizations.createOrganization(config)

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun createOrganizationWithDeprecatedDomainsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareCreateOrganizationTest(
      """{
        "name": "Organization Name",
        "allow_profiles_outside_organization": false,
        "domains": ["foo.com", "bar.com"]
      }"""
    )

    val config = CreateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(false)
      .domains(listOf("foo.com", "bar.com"))
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
        "domain_data": [
          {
            "domain": "foo.com",
            "state": "pending"
          },
          {
            "domain": "bar.com",
            "state": "pending"
          }
        ]
      }"""
    )

    val organization = workos.organizations.createOrganization(
      CreateOrganizationOptions(
        "Organization Name",
        false,
        listOf(
          OrganizationDomainDataOptions(
            "foo.com",
            OrganizationDomainDataState.PENDING
          ),
          OrganizationDomainDataOptions(
            "bar.com",
            OrganizationDomainDataState.PENDING
          )
        ),
        null
      )
    )

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun createOrganizationWithIdempotencyKeyReturnPayload() {
    val workos = createWorkOSClient()

    stubResponse(
      url = "/organizations",
      responseBody = """{
        "name": "Organization Name",
        "object": "organization",
        "id": "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8",
        "allow_profiles_outside_organization": false,
        "created_at": "2021-10-28T15:13:51.874Z",
        "updated_at": "2021-10-28T15:14:03.032Z",
        "domains": [
          {
            "domain": "foo.com",
            "object": "organization_domain",
            "id": "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
          }
        ]
      }""",
      requestHeaders = mapOf("Idempotency-Key" to "some-idempotency-key-value")
    )

    val organization = workos.organizations.createOrganization(
      CreateOrganizationOptions(
        "Organization Name",
        false,
        null,
        null
      ),
      CreateOrganizationRequestOptions(
        "some-idempotency-key-value"
      )
    )

    assertEquals("Organization Name", organization.name)
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

    assertEquals(organizationId, organizations[0].id)
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

  private fun prepareUpdateOrganizationTest(body: String): Map<String, String> {
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
      requestBody = body
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
        "domain_data": [
          {
            "domain": "example.com",
            "state": "verified"
          }
        ]
      }"""
    )

    val config = UpdateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(true)
      .domainData(listOf(
        OrganizationDomainDataOptions(
          "example.com",
          OrganizationDomainDataState.VERIFIED
        )
      ))
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
        "domain_data": [
          {
            "domain": "foo.com",
            "state": "verified"
          },
          {
            "domain": "bar.com",
            "state": "verified"
          }
        ]
      }"""
    )

    val organization = workos.organizations.updateOrganization(
      data["organizationId"]!!,
      UpdateOrganizationOptions(
        "Organization Name",
        false,
        listOf(
          OrganizationDomainDataOptions(
            "foo.com",
            OrganizationDomainDataState.VERIFIED
          ),
          OrganizationDomainDataOptions(
            "bar.com",
            OrganizationDomainDataState.VERIFIED
          )
        ),
        null,
      )
    )

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }

  @Test
  fun updateOrganizationWithDeprecatedDomainsShouldReturnPayload() {
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
        null,
        listOf("foo.com", "bar.com")
      )
    )

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
  }
}
