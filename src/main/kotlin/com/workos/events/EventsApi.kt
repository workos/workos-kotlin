package com.workos.events

import com.fasterxml.jackson.annotation.JsonInclude
import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.common.models.Order
import com.workos.events.models.EventList

/**
 * The EventsApi provides convenience methods for working with the WorkOS Events API.
 */
class EventsApi(private val workos: WorkOS) {
  /**
   * Parameters for [listEvents].
   *
   * @param events A list of event type strings to filter by.
   * @param organizationId Optional organization to filter events for.
   * @param after See [com.workos.common.http.PaginationParams]
   * @param before See [com.workos.common.http.PaginationParams]
   * @param limit See [com.workos.common.http.PaginationParams]
   * @param order See [com.workos.common.http.PaginationParams]
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class ListEventsOptions @JvmOverloads constructor(
    events: List<String>? = null,
    organizationId: String? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null,
    order: Order? = null
  ) : PaginationParams(after, before, limit, order) {
    init {
      if (events != null && events.isNotEmpty()) set("events", events.joinToString(","))
      if (organizationId != null) set("organization_id", organizationId)
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListEventsOptionsBuilder {
        return ListEventsOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [ListEventsOptions].
     */
    class ListEventsOptionsBuilder : PaginationParams.PaginationParamsBuilder<ListEventsOptions>(ListEventsOptions()) {
      /**
       * Sets the list of event types to filter on.
       */
      fun events(value: List<String>) = apply { this.params["events"] = value.joinToString(",") }

      /**
       * Filters events by organization.
       */
      fun organizationId(value: String) = apply { this.params["organization_id"] = value }

      override fun build(): ListEventsOptions {
        val options = ListEventsOptions()
        for ((key, value) in this.params) {
          options[key] = value
        }
        return options
      }
    }
  }

  /**
   * Retrieve a list of events.
   */
  fun listEvents(options: ListEventsOptions = ListEventsOptions()): EventList {
    val config = RequestConfig.builder()
      .params(options)
      .build()

    return workos.get("/events", EventList::class.java, config)
  }
}
