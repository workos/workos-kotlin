package com.workos.mfa

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import java.util.Date

class AuditLogsApi(private val workos: WorkOS) {
  @JsonInclude(Include.NON_NULL)
  class Actor @JvmOverloads constructor(
    val id: String,
    val type: String,
    val name: String? = null,
    val metadata: Map<String, String>? = null,
  )

  @JsonInclude(Include.NON_NULL)
  class Target @JvmOverloads constructor(
    val id: String,
    val type: String,
    val name: String? = null,
    val metadata: Map<String, String>? = null,
  )

  @JsonInclude(Include.NON_NULL)
  class Context constructor(
    val location: String,
    @JsonProperty("user_agent")
    val userAgent: String? = null,
  )

  /**
   * Parameters for the [createEvent] method.
   */
  @JsonInclude(Include.NON_NULL)
  class CreateAuditLogEventOptions @JvmOverloads constructor(
    val action: String,
    @JsonProperty("occurred_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    val occurredAt: Date,
    val actor: Actor,
    val targets: List<Target>,
    val context: Context,
    val version: Int? = 1,
    val metadata: Map<String, String>? = null,
  ) {
    /**
     * Builder class for [CreateAuditLogEventOptions].
     */
    class CreateAuditLogEventOptionsBuilder {
      private var action: String? = null
      private var occurredAt: Date? = null
      private var actor: Actor? = null
      private var targets: MutableList<Target> = mutableListOf<Target>()
      private var context: Context? = null
      private var version: Int? = 1
      private var metadata: Map<String, String>? = null

      /**
       * Sets the action.
       */
      fun action(value: String) = apply { action = value }

      /**
       * Sets the actor.
       */
      @JvmOverloads
      fun actor(
        id: String,
        type: String,
        name: String? = null,
        metadata: Map<String, String>? = null,
      ) = apply { actor = Actor(id, type, name, metadata) }

      /**
       * Sets the context.
       */
      @JvmOverloads
      fun context(
        location: String,
        userAgent: String? = null,
      ) = apply { context = Context(location, userAgent) }

      /**
       * Sets the occurred at date.
       */
      fun occurredAt(value: Date) = apply { occurredAt = value }

      /**
       * Adds a target to the targest list.
       */
      @JvmOverloads
      fun target(
        id: String,
        type: String,
        name: String? = null,
        metadata: Map<String, String>? = null,
      ) = apply { targets.add(Target(id, type, name, metadata)) }

      /**
       * Sets the context.
       */
      fun version(value: Int) = apply { version = value }

      /**
       * Sets the metadata.
       */
      fun metadata(value: Map<String, String>) = apply { metadata = value }

      /**
       * Creates a [EnrollFactorOptions] with the given builder parameters.
       */
      fun build(): CreateAuditLogEventOptions {
        if (action == null) {
          throw IllegalArgumentException("An action must be provided")
        }

        if (actor == null) {
          throw IllegalArgumentException("An actor must be provided")
        }

        if (context == null) {
          throw IllegalArgumentException("A context must be provided")
        }

        if (context == null) {
          throw IllegalArgumentException("A context must be provided")
        }

        if (occurredAt == null) {
          throw IllegalArgumentException("An occurred at date must be provided")
        }

        if (targets.isEmpty()) {
          throw IllegalArgumentException("At least one target must be provided")
        }

        return CreateAuditLogEventOptions(
          action = action!!,
          actor = actor!!,
          context = context!!,
          occurredAt = occurredAt!!,
          targets = targets,
          version = version,
          metadata = metadata,
        )
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CreateAuditLogEventOptionsBuilder {
        return CreateAuditLogEventOptionsBuilder()
      }
    }
  }

  /**
   * Emits an Audit Log event.
   */
  fun createEvent(createAuditLogEventOptions: CreateAuditLogEventOptions) {
    val config = RequestConfig.builder()
      .data(createAuditLogEventOptions)
      .build()

    workos.post("/audit_logs", config)
  }
}
