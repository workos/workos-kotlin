package com.workos.test.portal

import com.workos.portal.PortalApi
import com.workos.portal.models.Intent
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals

class PortalApiTest : TestBase() {
  private fun prepareGeneratePortalLinkTest(body: String): String {
    val portalLink = "https://id.workos.com/portal/launch?secret=JteZqfJZqUcgWGaYCC6iI0gW0"

    stubResponse(
      url = "/portal/generate_link",
      responseBody = """{
          "link": "$portalLink"
        }""",
      requestBody = body,
    )

    return portalLink
  }

  @Test
  fun createPortalLinkWithSsoShouldReturnPayload() {
    val workos = createWorkOSClient()

    val portalLink = prepareGeneratePortalLinkTest(
      """{
        "organization": "organizationId",
        "intent": "sso",
        "return_url": "returnUrl"
      }"""
    )

    val options = PortalApi.GeneratePortalLinkOptions.builder()
      .organization("organizationId")
      .intent(Intent.Sso)
      .returnUrl("returnUrl")
      .build()

    val response = workos.portal.generateLink(options)

    assertEquals(response.link, portalLink)
  }

  @Test
  fun createPortalLinkWithDsyncShouldReturnPayload() {
    val workos = createWorkOSClient()

    val portalLink = prepareGeneratePortalLinkTest(
      """{
        "organization": "organizationId",
        "intent": "dsync",
        "return_url": "returnUrl"
      }"""
    )

    val options = PortalApi.GeneratePortalLinkOptions.builder()
      .organization("organizationId")
      .intent(Intent.DirectorySync)
      .returnUrl("returnUrl")
      .build()

    val response = workos.portal.generateLink(options)

    assertEquals(response.link, portalLink)
  }

  @Test
  fun createPortalLinkWithAuditLogsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val portalLink = prepareGeneratePortalLinkTest(
      """{
        "organization": "organizationId",
        "intent": "audit_logs",
        "return_url": "returnUrl"
      }"""
    )

    val options = PortalApi.GeneratePortalLinkOptions.builder()
      .organization("organizationId")
      .intent(Intent.AuditLogs)
      .returnUrl("returnUrl")
      .build()

    val response = workos.portal.generateLink(options)

    assertEquals(response.link, portalLink)
  }

  @Test
  fun createPortalLinkWithRawOptionsShouldReturnPayload() {
    val workos = createWorkOSClient()

    val portalLink = prepareGeneratePortalLinkTest(
      """{
        "organization": "organizationId",
        "intent": "sso",
        "return_url": "returnUrl"
      }"""
    )

    val options = PortalApi.GeneratePortalLinkOptions(
      organization = "organizationId",
      intent = Intent.Sso,
      returnUrl = "returnUrl"
    )

    val response = workos.portal.generateLink(options)

    assertEquals(response.link, portalLink)
  }

  @Test
  fun createPortalLinkWithSuccessUrlShouldReturnPayload() {
    val workos = createWorkOSClient()

    val portalLink = prepareGeneratePortalLinkTest(
      """{
        "organization": "organizationId",
        "intent": "sso",
        "return_url": "returnUrl",
        "success_url": "successUrl"
      }"""
    )

    val options = PortalApi.GeneratePortalLinkOptions(
      organization = "organizationId",
      intent = Intent.Sso,
      returnUrl = "returnUrl",
      successUrl = "successUrl"
    )

    val response = workos.portal.generateLink(options)

    assertEquals(response.link, portalLink)
  }

  @Test
  fun buildGeneratePortalLinkOptionsWithNoOrganizationShouldThrow() {

    assertThrows(IllegalArgumentException::class.java) {

      PortalApi.GeneratePortalLinkOptions
        .builder()
        .intent(Intent.Sso)
        .build()
    }
  }

  @Test
  fun buildGeneratePortalLinkOptionsWithNoIntentShouldThrow() {
    assertThrows(IllegalArgumentException::class.java) {
      PortalApi.GeneratePortalLinkOptions
        .builder()
        .organization("organizationId")
        .build()
    }
  }
}
