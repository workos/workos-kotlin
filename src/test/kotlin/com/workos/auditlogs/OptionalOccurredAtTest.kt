// @oagen-ignore-file

package com.workos.auditlogs

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.workos.common.json.ObjectMapperFactory
import com.workos.models.AuditLogEvent
import com.workos.models.AuditLogEventActor
import com.workos.models.AuditLogEventContext
import com.workos.test.TestBase
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class OptionalOccurredAtTest : TestBase() {
  @Test
  fun `defaulted timestamp is absent on the wire`() {
    stubResponse("POST", "/audit_logs/events", 200, "{\"success\":true}")
    val event =
      AuditLogEvent(
        action = "user.signed_in",
        actor = AuditLogEventActor("user_1", "user"),
        targets = emptyList(),
        context = AuditLogEventContext("127.0.0.1")
      )

    AuditLogs(createWorkOSClient()).createEvent("org_123", event)

    val request = wireMockRule.findAll(postRequestedFor(urlPathMatching("/audit_logs/events"))).single()
    val body = ObjectMapperFactory.default.readTree(request.bodyAsString)
    assertFalse(body["event"].has("occurred_at"), request.bodyAsString)
  }

  @Test
  fun `old positional constructor preserves an explicit timestamp`() {
    stubResponse("POST", "/audit_logs/events", 200, "{\"success\":true}")
    val timestamp = "2026-11-03T12:34:56.789Z"
    val event =
      AuditLogEvent(
        "user.signed_in",
        OffsetDateTime.parse(timestamp),
        AuditLogEventActor("user_1", "user"),
        emptyList(),
        AuditLogEventContext("127.0.0.1")
      )

    AuditLogs(createWorkOSClient()).createEvent("org_123", event)

    wireMockRule.verify(
      postRequestedFor(urlPathMatching("/audit_logs/events"))
        .withRequestBody(matchingJsonPath("$.event.occurred_at", equalTo(timestamp)))
    )
  }
}
