// @oagen-ignore-file
package com.workos.common.http

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.workos.groups.Groups
import com.workos.test.TestBase
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BaseClientPathSafetyTest : TestBase() {
  @Test
  fun `group deletion rejects empty and dot identifiers without sending requests`() {
    wireMockRule.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(204)))
    val groups = Groups(createWorkOSClient())
    for (id in listOf("..", ".", "")) {
      assertThrows(IllegalArgumentException::class.java) { groups.deleteOrganizationGroup("org_123", id) }
      assertThrows(IllegalArgumentException::class.java) { groups.deleteOrganizationGroup(id, "group_123") }
      assertThrows(IllegalArgumentException::class.java) { groups.deleteOrganizationMembership("org_123", id, "om_123") }
      assertThrows(IllegalArgumentException::class.java) { groups.deleteOrganizationMembership("org_123", "group_123", id) }
    }
    assertEquals(0, wireMockRule.allServeEvents.size)
  }

  @Test
  fun `safe group identifiers retain their encoded path on the wire`() {
    wireMockRule.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(204)))
    val groups = Groups(createWorkOSClient())
    val ids = listOf("group_123", "group.name", "...", "%2e", "%2E%2e", ".%2e", "%2e.", "../admin", "..\\admin", "a?b#c", "a b+café")
    for (id in ids) {
      val path = "/organizations/org_123/groups/${encodePathSegment(id)}"
      groups.deleteOrganizationGroup("org_123", id)
      wireMockRule.verify(1, deleteRequestedFor(urlEqualTo(path)))
    }
    assertEquals(ids.size, wireMockRule.allServeEvents.size)
  }

  @Test
  fun `raw request paths reject literal and encoded dot segments before transport`() {
    wireMockRule.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(204)))
    val client = createWorkOSClient().baseClient
    for (segment in listOf(".", "..", "%2e", "%2E", "%2e%2e", "%2E%2E", "%2e%2E", ".%2e", "%2E.")) {
      for (suffix in listOf("", "/organization-memberships/om_123")) {
        val path = "/organizations/org_123/groups/$segment$suffix"
        assertThrows(IllegalArgumentException::class.java, { client.requestVoid(RequestConfig("DELETE", path)) }, path)
      }
    }
    assertEquals(0, wireMockRule.allServeEvents.size)
  }

  @Test
  fun `raw request paths reject other canonicalization changes before transport`() {
    wireMockRule.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(204)))
    val client = createWorkOSClient().baseClient
    for (path in listOf("/groups/a\\..\\..", "/groups/a?b", "/groups/a#b", "/groups/a b", "/groups/a\nb")) {
      assertThrows(IllegalArgumentException::class.java, { client.requestVoid(RequestConfig("DELETE", path)) }, path)
    }
    assertEquals(0, wireMockRule.allServeEvents.size)
  }

  @Test
  fun `base URL path prefixes are rejected for client and per-request configuration`() {
    wireMockRule.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(204)))
    val client = BaseClient("sk_test", "${wireMockRule.baseUrl()}/prefix", OkHttpClient())
    assertThrows(IllegalArgumentException::class.java) { client.requestVoid(RequestConfig("DELETE", "/groups/group_123")) }
    assertThrows(IllegalArgumentException::class.java) {
      createWorkOSClient().baseClient.requestVoid(
        RequestConfig("DELETE", "/groups/group_123", requestOptions = RequestOptions(baseUrl = "${wireMockRule.baseUrl()}/prefix"))
      )
    }
    assertEquals(0, wireMockRule.allServeEvents.size)
  }

  @Test
  fun `trailing base URL slash and query parameters preserve intended path`() {
    wireMockRule.stubFor(any(anyUrl()).willReturn(aResponse().withStatus(204)))
    createWorkOSClient().baseClient.requestVoid(
      RequestConfig(
        "DELETE",
        "/groups/group_123",
        queryParams = listOf("cursor" to "../a?b#c"),
        requestOptions = RequestOptions(baseUrl = "${wireMockRule.baseUrl()}/")
      )
    )
    wireMockRule.verify(1, deleteRequestedFor(urlEqualTo("/groups/group_123?cursor=..%2Fa%3Fb%23c")))
  }
}
