package com.workos.test.organizations

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.workos.common.exceptions.UnauthorizedException
import com.workos.organizations.OrganizationsApi
import com.workos.organizations.OrganizationsApi.CreateOrganizationOptions
import com.workos.organizations.OrganizationsApi.CreateOrganizationRequestOptions
import com.workos.organizations.OrganizationsApi.UpdateOrganizationOptions
import com.workos.organizations.types.OrganizationDomainDataOptions
import com.workos.organizations.types.OrganizationDomainDataState
import com.workos.organizations.types.OrganizationDomainState
import com.workos.organizations.types.OrganizationDomainVerificationStrategy
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OrganizationsApiTest : TestBase() {
  private fun prepareCreateOrganizationTest(body: String): Map<String, String> {
    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"
    val organizationDomainId = "org_domain_01EHT88Z8WZEFWYPM6EC9BX2R8"
    val organizationDomainName = "Test Organization"
    val organizationExternalId = "external_12345"

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
        ],
        "external_id": "$organizationExternalId",
        "metadata": {
          "tier": "pro"
        }
      }""",
      requestBody = body
    )

    return mapOf(
      "organizationId" to organizationId,
      "organizationDomainId" to organizationDomainId,
      "organizationDomainName" to organizationDomainName,
      "externalId" to organizationExternalId,
      "metadataTier" to "pro",
    )
  }

  @Test
  fun createOrganizationWithDefaultsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val data = prepareCreateOrganizationTest(
      """{}"""
    )

    val organization = workos.organizations.createOrganization()
    val metadata = organization.metadata

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
    assertEquals(data["externalId"], organization.externalId)
    assertEquals(data["metadataTier"], metadata?.get("tier"))
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
        ],
        "external_id": "external_12345",
        "metadata": {
          "tier": "pro"
        }
      }"""
    )

    val config = CreateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(true)
      .domainData(
        listOf(
          OrganizationDomainDataOptions(
            "example.com",
            OrganizationDomainDataState.Pending
          )
        )
      )
      .externalId("external_12345")
      .metadata(mapOf("tier" to "pro"))
      .build()

    val organization = workos.organizations.createOrganization(config)
    val metadata = organization.metadata

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
    assertEquals(data["externalId"], organization.externalId)
    assertEquals(data["metadataTier"], metadata?.get("tier"))
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
    val metadata = organization.metadata

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
    assertEquals(data["externalId"], organization.externalId)
    assertEquals(data["metadataTier"], metadata?.get("tier"))
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
            OrganizationDomainDataState.Pending
          ),
          OrganizationDomainDataOptions(
            "bar.com",
            OrganizationDomainDataState.Pending
          )
        ),
        null
      )
    )
    val metadata = organization.metadata

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
    assertEquals(data["externalId"], organization.externalId)
    assertEquals(data["metadataTier"], metadata?.get("tier"))
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
    assertNull(organization.externalId)
    assertNull(organization.metadata)
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
            "id": "$organizationDomainId",
            "state": "verified",
            "verification_strategy": "dns",
            "verification_token": "rqURsMUCuiaSggGyed8ZAnMk"
          }
        ],
        "external_id": "external_12345"
      }"""
    )

    val organization = workos.organizations.getOrganization(organizationId)

    assertEquals(organizationId, organization.id)
    assertEquals(organizationDomainName, organization.name)
    assertEquals(organizationDomainId, organization.domains[0].id)
    assertEquals(OrganizationDomainState.Verified, organization.domains[0].state)
    assertEquals(OrganizationDomainVerificationStrategy.Dns, organization.domains[0].verificationStrategy)
    assertEquals("rqURsMUCuiaSggGyed8ZAnMk", organization.domains[0].verificationToken)
    assertEquals("external_12345", organization.externalId)
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
                "id": "$organizationDomainId",
                "state": "verified",
                "verification_strategy": "dns",
                "verification_token": "rqURsMUCuiaSggGyed8ZAnMk"
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
    assertEquals(OrganizationDomainState.Verified, organizations.get(0).domains[0].state)
    assertEquals(OrganizationDomainVerificationStrategy.Dns, organizations.get(0).domains[0].verificationStrategy)
    assertEquals("rqURsMUCuiaSggGyed8ZAnMk", organizations.get(0).domains[0].verificationToken)
    assertNull(organizations.get(0).externalId)
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
                "id": "$organizationDomainId",
                "state": "verified",
                "verification_strategy": "dns",
                "verification_token": "rqURsMUCuiaSggGyed8ZAnMk"
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
                "id": "$organizationDomainId",
                "state": "verified",
                "verification_strategy": "dns",
                "verification_token": "rqURsMUCuiaSggGyed8ZAnMk"
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
    val organizationStripeCustomerId = "cus_R9qWAGMQ6nGE7V"
    val organizationExternalId = "external_12345"

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
        ],
        "stripe_customer_id": "cus_R9qWAGMQ6nGE7V",
        "external_id": "$organizationExternalId"
      }""",
      requestBody = body
    )

    return mapOf(
      "organizationId" to organizationId,
      "organizationDomainId" to organizationDomainId,
      "organizationDomainName" to organizationDomainName,
      "organizationStripeCustomerId" to organizationStripeCustomerId,
      "organizationExternalId" to organizationExternalId,
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
    assertEquals(data["organizationStripeCustomerId"], organization.stripeCustomerId)
    assertEquals(data["organizationExternalId"], organization.externalId)
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
        ],
        "stripe_customer_id": "cus_R9qWAGMQ6nGE7V",
        "external_id": "external_12345"
      }"""
    )

    val config = UpdateOrganizationOptions.builder()
      .name("Organization Name")
      .allowProfilesOutsideOrganization(true)
      .domainData(
        listOf(
          OrganizationDomainDataOptions(
            "example.com",
            OrganizationDomainDataState.Verified
          )
        )
      )
      .stripeCustomerId("cus_R9qWAGMQ6nGE7V")
      .externalId("external_12345")
      .build()

    val organization = workos.organizations.updateOrganization(data["organizationId"]!!, config)

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
    assertEquals(data["organizationStripeCustomerId"], organization.stripeCustomerId)
    assertEquals(data["organizationExternalId"], organization.externalId)
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
    assertEquals(data["organizationStripeCustomerId"], organization.stripeCustomerId)
    assertEquals(data["organizationExternalId"], organization.externalId)
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
        ],
        "stripe_customer_id": "cus_R9qWAGMQ6nGE7V",
        "external_id": "external_12345"
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
            OrganizationDomainDataState.Verified
          ),
          OrganizationDomainDataOptions(
            "bar.com",
            OrganizationDomainDataState.Verified
          )
        ),
        null,
        "cus_R9qWAGMQ6nGE7V",
        "external_12345"
      )
    )

    assertEquals(data["organizationId"], organization.id)
    assertEquals(data["organizationDomainName"], organization.name)
    assertEquals(data["organizationDomainId"], organization.domains[0].id)
    assertEquals(data["organizationStripeCustomerId"], organization.stripeCustomerId)
    assertEquals(data["organizationExternalId"], organization.externalId)
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
    assertEquals(data["organizationStripeCustomerId"], organization.stripeCustomerId)
    assertEquals(data["organizationExternalId"], organization.externalId)
  }

  @Test
  fun listOrganizationRolesShouldReturnPayload() {
    val workos = createWorkOSClient()

    val organizationId = "org_01FJYCNTB6VC4K5R8BTF86286Q"

    stubResponse(
      "/organizations/$organizationId/roles",
      """{
        "object": "list",
        "data": [
          {
            "object": "role",
            "id": "role_01EHQMYV6MBK39QC5PZXHY59C5",
            "name": "Admin",
            "slug": "admin",
            "description": null,
            "type": "EnvironmentRole",
            "created_at": "2024-01-01T00:00:00.000Z",
            "updated_at": "2024-01-01T00:00:00.000Z"
          },
          {
            "object": "role",
            "id": "role_01EHQMYV6MBK39QC5PZXHY59C3",
            "name": "Member",
            "slug": "member",
            "description": null,
            "type": "EnvironmentRole",
            "created_at": "2024-01-01T00:00:00.000Z",
            "updated_at": "2024-01-01T00:00:00.000Z"
          },
          {
            "object": "role",
            "id": "role_01EHQMYV6MBK39QC5PZXHY59C3",
            "name": "OrganizationMember",
            "slug": "org-member",
            "description": null,
            "type": "OrganizationRole",
            "created_at": "2024-01-01T00:00:00.000Z",
            "updated_at": "2024-01-01T00:00:00.000Z"
          }
        ]
      }"""
    )

    val (roles) = workos.organizations.listOrganizationRoles(organizationId)

    assertEquals("role_01EHQMYV6MBK39QC5PZXHY59C5", roles.get(0).id)
    assertEquals("Admin", roles.get(0).name)
    assertEquals("admin", roles.get(0).slug)
  }
}
