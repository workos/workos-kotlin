package com.workos.organizations

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.PaginationParams
import com.workos.common.http.RequestConfig
import com.workos.common.models.Order
import com.workos.organizations.models.Organization
import com.workos.organizations.models.OrganizationList

/**
 * The OrganizationsApi provides convenience methods for working with WorkOS Organizations.
 */
class OrganizationsApi(private val workos: WorkOS) {
  /**
   * Parameters for [createOrganization].
   * Use `CreateOrganizationOptions.builder()` to create a new builder instance.
   *
   * @param name The name of the organization.
   * @param allowProfilesOutsideOrganization Whether the Connections within this Organization should allow Profiles that do not have a domain that is present in the set of the Organization's User Email Domains.
   * @param domains A list of domains for the organization.
   */
  @JsonInclude(Include.NON_NULL)
  class CreateOrganizationOptions @JvmOverloads constructor(
    val name: String? = null,

    @JsonProperty("allow_profiles_outside_organization")
    val allowProfilesOutsideOrganization: Boolean? = null,

    val domains: List<String>? = null

  ) {
    /**
     * Builder class for creating [CreateOrganizationOptions].
     */
    class CreateOrganizationOptionsBuilder {
      private var name: String? = null

      private var allowProfilesOutsideOrganization: Boolean? = null

      private var domains: List<String>? = null

      /**
       * Sets the name of the organization.
       */
      fun name(value: String) = apply { name = value }

      /**
       * Sets whether profiles with unmatched domains can exist within the organization.
       */
      fun allowProfilesOutsideOrganization(value: Boolean) = apply { allowProfilesOutsideOrganization = value }

      /**
       * Sets the list of domains for the organization.
       */
      fun domains(value: List<String>) = apply { domains = value }

      /**
       * Creates an instance of [CreateOrganizationOptions] with the given params.
       */
      fun build(): CreateOrganizationOptions {
        return CreateOrganizationOptions(name, allowProfilesOutsideOrganization, domains)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CreateOrganizationOptionsBuilder {
        return CreateOrganizationOptionsBuilder()
      }
    }
  }

  @JsonInclude(Include.NON_NULL)
  class CreateOrganizationRequestOptions constructor(
    val idempotencyKey: String? = null
  ) {
    /**
     * Builder class for [CreateOrganizationRequestOptions].
     */
    class CreateOrganizationRequestOptionsBuilder {
      private var idempotencyKey: String? = null

      /**
       * Sets the idempotencyKey.
       */
      fun idempotencyKey(value: String) = apply { idempotencyKey = value }

      /**
       * Creates a [CreateOrganizationRequestOptions] with the given builder parameters.
       */
      fun build(): CreateOrganizationRequestOptions {
        return CreateOrganizationRequestOptions(idempotencyKey)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): CreateOrganizationRequestOptionsBuilder {
        return CreateOrganizationRequestOptionsBuilder()
      }
    }
  }

  /**
   * Creates a new organization.
   */

  @JvmOverloads
  fun createOrganization(
    options: CreateOrganizationOptions = CreateOrganizationOptions(),
    requestOptions: CreateOrganizationRequestOptions ? = null
  ): Organization {
    val config = RequestConfig.builder()
      .data(options)

    if (requestOptions != null) {
      config.headers(mapOf("Idempotency-Key" to requestOptions.idempotencyKey))
    }

    return workos.post("/organizations", Organization::class.java, config.build())
  }

  /**
   * Deletes a single organization by id.
   */
  fun deleteOrganization(id: String) {
    workos.delete("/organizations/$id")
  }

  /**
   * Fetches a single organization by id.
   */
  fun getOrganization(id: String): Organization {
    return workos.get("/organizations/$id", Organization::class.java)
  }

  /**
   * Parameters for [listOrganizations] method.
   * Use `ListOrganizationsOptions.builder()` to create a new builder instance.
   *
   * @param domains list of domains to filter by.
   * @param after @see [com.workos.common.http.PaginationParams]
   * @param before @see [com.workos.common.http.PaginationParams]
   * @param limit @see [com.workos.common.http.PaginationParams]
   * @param order @see [com.workos.common.http.PaginationParams]
   */
  class ListOrganizationsOptions @JvmOverloads constructor(
    domains: List<String>? = null,
    after: String? = null,
    before: String? = null,
    limit: Int? = null,
    order: Order? = null
  ) : PaginationParams(after, before, limit, order) {
    init {
      if (domains != null) set("domains", domains.joinToString(","))
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ListOrganizationsOptionsBuilder {
        return ListOrganizationsOptionsBuilder()
      }
    }

    /**
     * Builder class for creating [ListOrganizationsOptions]
     */
    class ListOrganizationsOptionsBuilder : PaginationParams.PaginationParamsBuilder<ListOrganizationsOptions>(ListOrganizationsOptions()) {
      /**
       * Sets the list of domains to filter on.
       */
      fun domains(value: List<String>) = apply { this.params["domains"] = value.joinToString(",") }
    }
  }

  /**
   * Retrieve a list of organizations that have connections configured
   * within your WorkOS dashboard.
   */
  fun listOrganizations(options: ListOrganizationsOptions = ListOrganizationsOptions()): OrganizationList {
    val config = RequestConfig.builder()
      .params(options)
      .build()

    return workos.get("/organizations", OrganizationList::class.java, config)
  }

  /**
   * Parameters [updateOrganization] method.
   * Use `UpdateOrganizationOptions.builder()` to create a new builder instance.
   *
   * @param name The name of the organization.
   * @param allowProfilesOutsideOrganization Whether the Connections within this Organization should allow Profiles that do not have a domain that is present in the set of the Organization's User Email Domains.
   * @param domains A list of domains for the organization.
   */
  @JsonInclude(Include.NON_NULL)
  class UpdateOrganizationOptions @JvmOverloads constructor(
    val name: String? = null,

    @JsonProperty("allow_profiles_outside_organization")
    val allowProfilesOutsideOrganization: Boolean? = null,

    val domains: List<String>? = null
  ) {
    /**
     * Builder class for [UpdateOrganizationOptions].
     */
    class UpdateOrganizationOptionsBuilder {
      private var name: String? = null

      private var allowProfilesOutsideOrganization: Boolean? = null

      private var domains: List<String>? = null

      /**
       * Sets the name of the organization.
       */
      fun name(value: String) = apply { name = value }

      /**
       * Sets whether profiles with unmatched domains can exist within the organization.
       */
      fun allowProfilesOutsideOrganization(value: Boolean) = apply { allowProfilesOutsideOrganization = value }

      /**
       * Sets the list of domains for the organization.
       */
      fun domains(value: List<String>) = apply { domains = value }

      /**
       * Creates an instance of [UpdateOrganizationOptions].
       */
      fun build(): UpdateOrganizationOptions {
        return UpdateOrganizationOptions(name, allowProfilesOutsideOrganization, domains)
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): UpdateOrganizationOptionsBuilder {
        return UpdateOrganizationOptionsBuilder()
      }
    }
  }

  /**
   * Updates a single organization.
   */
  fun updateOrganization(
    id: String,
    options: UpdateOrganizationOptions = UpdateOrganizationOptions()
  ): Organization {
    val config = RequestConfig.builder()
      .data(options)
      .build()

    return workos.put("/organizations/$id", Organization::class.java, config)
  }
}
