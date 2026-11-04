// @oagen-ignore-file
package com.workos.auditlogs;

import com.workos.models.AuditLogEvent;
import com.workos.models.AuditLogEventActor;
import com.workos.models.AuditLogEventContext;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OptionalOccurredAtJavaTest {
  @Test
  void legacyConstructorKeepsTimestampInSecondPosition() {
    OffsetDateTime timestamp = OffsetDateTime.parse("2026-11-03T12:34:56.789Z");
    AuditLogEvent event = new AuditLogEvent(
        "user.signed_in", timestamp,
        new AuditLogEventActor("user_1", "user", null, null),
        Collections.emptyList(), new AuditLogEventContext("127.0.0.1", null),
        null, null);
    assertEquals(timestamp, event.getOccurredAt());
  }

  @Test
  void legacyConstructorAcceptsNullForOmittedTimestamp() {
    AuditLogEvent event = new AuditLogEvent(
        "user.signed_in", null,
        new AuditLogEventActor("user_1", "user", null, null),
        Collections.emptyList(), new AuditLogEventContext("127.0.0.1", null),
        null, null);
    assertNull(event.getOccurredAt());
  }
}
