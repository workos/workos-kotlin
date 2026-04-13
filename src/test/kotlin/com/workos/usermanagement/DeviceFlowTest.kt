// @oagen-ignore-file
package com.workos.usermanagement

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.workos.test.TestBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeviceFlowTest : TestBase() {
  private fun api(): UserManagement = UserManagement(createWorkOSClient())

  @BeforeEach
  fun stubSleep() {
    sleepFor = { /* no-op — tests simulate polling without real waits */ }
  }

  @AfterEach
  fun restoreSleep() {
    sleepFor = { Thread.sleep(it) }
  }

  @Test
  fun `pollDeviceAuthorization returns the auth response once the user approves`() {
    val scenario = "auth"
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .inScenario(scenario)
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse().withStatus(400).withBody("""{"code":"authorization_pending"}"""))
        .willSetStateTo("pending-2")
    )
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .inScenario(scenario)
        .whenScenarioStateIs("pending-2")
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(authOk())
        )
    )
    val result =
      api().pollDeviceAuthorization(
        PollDeviceAuthorizationOptions(deviceCode = "dev_1", intervalSeconds = 1, expiresInSeconds = 60)
      )
    assertEquals("a.b.c", result.accessToken)
  }

  @Test
  fun `pollDeviceAuthorization throws DeviceFlowException on access_denied`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .willReturn(aResponse().withStatus(400).withBody("""{"code":"access_denied","message":"denied"}"""))
    )
    val ex =
      assertThrows(DeviceFlowException::class.java) {
        api().pollDeviceAuthorization(
          PollDeviceAuthorizationOptions(deviceCode = "d", intervalSeconds = 1, expiresInSeconds = 10)
        )
      }
    assertEquals(DeviceFlowFailureReason.ACCESS_DENIED, ex.reason)
  }

  @Test
  fun `pollDeviceAuthorization throws DeviceFlowException on expired_token`() {
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .willReturn(aResponse().withStatus(400).withBody("""{"code":"expired_token"}"""))
    )
    val ex =
      assertThrows(DeviceFlowException::class.java) {
        api().pollDeviceAuthorization(
          PollDeviceAuthorizationOptions(deviceCode = "d", intervalSeconds = 1, expiresInSeconds = 10)
        )
      }
    assertEquals(DeviceFlowFailureReason.EXPIRED_TOKEN, ex.reason)
  }

  @Test
  fun `pollDeviceAuthorization honors slow_down by extending the interval`() {
    // Just prove slow_down doesn't throw and that we subsequently reach success.
    val scenario = "slow"
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .inScenario(scenario)
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse().withStatus(400).withBody("""{"code":"slow_down"}"""))
        .willSetStateTo("next")
    )
    wireMockRule.stubFor(
      post(urlPathEqualTo("/user_management/authenticate"))
        .inScenario(scenario)
        .whenScenarioStateIs("next")
        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(authOk()))
    )
    val result =
      api().pollDeviceAuthorization(
        PollDeviceAuthorizationOptions(deviceCode = "d", intervalSeconds = 1, expiresInSeconds = 60)
      )
    assertEquals("a.b.c", result.accessToken)
  }

  private fun authOk(): String =
    """
    {
      "user": {
        "object": "user",
        "id": "u",
        "email": "u@e.com",
        "first_name": null,
        "last_name": null,
        "email_verified": true,
        "profile_picture_url": null,
        "last_sign_in_at": null,
        "created_at": "2024-01-01T00:00:00Z",
        "updated_at": "2024-01-01T00:00:00Z",
        "external_id": null,
        "metadata": {}
      },
      "access_token": "a.b.c",
      "refresh_token": "r"
    }
    """.trimIndent()
}
