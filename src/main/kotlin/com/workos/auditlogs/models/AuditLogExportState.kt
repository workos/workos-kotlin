package com.workos.auditlogs.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of states for a [AuditLogExport]
 *
 * @param state The AuditLogExport State string value.
 */
enum class AuditLogExportState(@JsonValue val state: String) {
  /**
   * The export is still being generated.
   */
  Pending("pending"),
  /**
   * The export is ready for download.
   */
  Ready("ready"),
  /**
   * The export has run into an unexpected error.
   */
  Error("error"),
}
