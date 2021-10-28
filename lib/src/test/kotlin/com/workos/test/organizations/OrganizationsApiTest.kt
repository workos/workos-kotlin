package com.workos.test.organizations

import com.github.tomakehurst.wiremock.client.WireMock.* // ktlint-disable no-wildcard-imports
import com.workos.common.exceptions.UnauthorizedException
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class OrganizationsApiTest : TestBase() {
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
}
