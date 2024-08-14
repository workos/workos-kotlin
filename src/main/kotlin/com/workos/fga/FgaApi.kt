package com.workos.fga

import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.fga.models.CheckResponse
import com.workos.fga.models.QueryResponse
import com.workos.fga.models.Resource
import com.workos.fga.models.Resources
import com.workos.fga.models.Warrants
import com.workos.fga.models.WriteWarrantResponse
import com.workos.fga.types.CheckBatchOptions
import com.workos.fga.types.CheckOptions
import com.workos.fga.types.CheckRequestOptions
import com.workos.fga.types.CreateResourceOptions
import com.workos.fga.types.ListResourcesOptions
import com.workos.fga.types.ListWarrantsOptions
import com.workos.fga.types.ListWarrantsRequestOptions
import com.workos.fga.types.QueryOptions
import com.workos.fga.types.QueryRequestOptions
import com.workos.fga.types.UpdateResourceOptions
import com.workos.fga.types.WriteWarrantOptions

class FgaApi(private val workos: WorkOS) {
  /** Get an existing resource. */
  fun getResource(resourceType: String, resourceId: String): Resource {
    return workos.get("/fga/v1/resources/$resourceType/$resourceId", Resource::class.java)
  }

  /** Get a list of all the existing resources matching the criteria specified */
  fun listResources(options: ListResourcesOptions? = null): Resources {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListResourcesOptions()) as Map<String, String>

    return workos.get(
      "/fga/v1/resources",
      Resources::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** Create a new resource in the current environment. */
  fun createResource(options: CreateResourceOptions): Resource {
    return workos.post(
      "/fga/v1/resources",
      Resource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Update an existing resource. */
  fun updateResource(resourceType: String, resourceId: String, options: UpdateResourceOptions): Resource {
    return workos.put(
      "/fga/v1/resources/$resourceType/$resourceId",
      Resource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a resource in the current environment. */
  fun deleteResource(resourceType: String, resourceId: String) {
    workos.delete("/fga/v1/resources/$resourceType/$resourceId")
  }

  /** Get a list of all existing warrants matching the criteria specified */
  fun listWarrants(options: ListWarrantsOptions? = null, requestOptions: ListWarrantsRequestOptions? = null): Warrants {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListWarrantsOptions()) as Map<String, String>

    val config = RequestConfig.builder()
      .params(params)

    if (requestOptions != null) {
      config.headers(mapOf("Warrant-Token" to requestOptions.warrantToken))
    }

    return workos.get(
      "/fga/v1/warrants",
      Warrants::class.java,
      config.build()
    )
  }

  /** Perform a warrant write (create/delete) in the current environment */
  fun writeWarrant(options: WriteWarrantOptions): WriteWarrantResponse {
    return workos.post(
      "/fga/v1/warrants",
      WriteWarrantResponse::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Performs multiple warrant writes in the current environment */
  fun batchWriteWarrants(options: List<WriteWarrantOptions>): WriteWarrantResponse {
    return workos.post(
      "/fga/v1/warrants",
      WriteWarrantResponse::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Perform a warrant check in the current environment */
  fun check(options: CheckOptions, requestOptions: CheckRequestOptions? = null): CheckResponse {
    val config = RequestConfig.builder()
      .data(options)

    if (requestOptions != null) {
      config.headers(mapOf("Warrant-Token" to requestOptions.warrantToken))
    }

    return workos.post("/fga/v1/check", CheckResponse::class.java, config.build())
  }

  /** Perform a batch warrant check in the current environment */
  fun checkBatch(options: CheckBatchOptions, requestOptions: CheckRequestOptions? = null): Array<CheckResponse> {
    val config = RequestConfig.builder()
      .data(options)

    if (requestOptions != null) {
      config.headers(mapOf("Warrant-Token" to requestOptions.warrantToken))
    }

    return workos.post("/fga/v1/check", Array<CheckResponse>::class.java, config.build())
  }

  /** Perform a query in the current environment */
  fun query(options: QueryOptions, requestOptions: QueryRequestOptions? = null): QueryResponse {
    val config = RequestConfig.builder()
      .data(options)

    if (requestOptions != null) {
      config.headers(mapOf("Warrant-Token" to requestOptions.warrantToken))
    }

    return workos.post("/fga/v1/query", QueryResponse::class.java, config.build())
  }
}
