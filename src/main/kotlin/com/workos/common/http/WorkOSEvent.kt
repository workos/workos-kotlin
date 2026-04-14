// @oagen-ignore-file
package com.workos.common.http

import java.time.OffsetDateTime

/**
 * Common interface for all webhook/event envelope models.
 *
 * Every event model emitted by oagen shares the same envelope shape:
 *
 * ```json
 * { "id": "...", "event": "...", "data": { ... }, "object": "event", "created_at": "..." }
 * ```
 *
 * This interface exposes the shared fields so callers can write generic
 * event handlers without switching on every concrete type:
 *
 * ```kotlin
 * fun handleEvent(event: WorkOSEvent) {
 *   println("Received ${event.event} at ${event.createdAt}")
 * }
 * ```
 *
 * Java users access the fields through the generated `@JvmField` annotations
 * on each concrete data class.
 */
interface WorkOSEvent {
  val id: String
  val event: String
  val createdAt: OffsetDateTime
}
