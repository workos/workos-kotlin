package com.workos.auditlogs.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.common.constants.JsonFormatterConstants
import java.util.Date

/**
 * Represents a WorkOS AuditLogExport resource. This class is not meant to be
 * instantiated directly.
 *
 * @param obj The unique object identifier type of the record.
 * @param id The unique identifier for the AuditLogExport.
 * @param state The state of the AuditLogExport.
 * @param url The CSV URL of the AuditLogExport.
 * @param createdAt The timestamp of when the AuditLogExport was created.
 * @param updatedAt The timestamp of when the AuditLogExport was updated.
 */
data class AuditLogExport
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  val obj: String = "audit_log_export",

  @JvmField
  val id: String,

  @JvmField
  val state: AuditLogExportState,

  @JvmField
  val url: String?,

  @JvmField
  @JsonProperty("created_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormatterConstants.ISO_SIMPLE_DATE_PATTERN, timezone = "UTC")
  val createdAt: Date,

  @JvmField
  @JsonProperty("updated_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonFormatterConstants.ISO_SIMPLE_DATE_PATTERN, timezone = "UTC")
  val updatedAt: Date
)
