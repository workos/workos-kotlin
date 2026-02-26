package com.workos.authorization

import com.workos.WorkOS
import com.workos.authorization.models.AuthorizationResource
import com.workos.authorization.models.AuthorizationResourceList
import com.workos.authorization.types.CreateAuthorizationResourceOptions
import com.workos.authorization.types.ListAuthorizationResourcesOptions
import com.workos.authorization.types.UpdateAuthorizationResourceOptions
import com.workos.common.http.RequestConfig

class AuthorizationApi(private val workos: WorkOS) {

  /** Get a resource by internal ID. */
  fun getResource(resourceId: String): AuthorizationResource {
    return workos.get(
      "/authorization/resources/$resourceId",
      AuthorizationResource::class.java
    )
  }

  /** Create a new authorization resource. */
  fun createResource(options: CreateAuthorizationResourceOptions): AuthorizationResource {
    return workos.post(
      "/authorization/resources",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Update a resource by internal ID. */
  fun updateResource(resourceId: String, options: UpdateAuthorizationResourceOptions): AuthorizationResource {
    return workos.patch(
      "/authorization/resources/$resourceId",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a resource by internal ID. */
  fun deleteResource(resourceId: String, cascadeDelete: Boolean = false) {
    val config = if (cascadeDelete) {
      RequestConfig.builder().params(mapOf("cascade_delete" to "true")).build()
    } else {
      null
    }
    workos.delete("/authorization/resources/$resourceId", config)
  }

  /** List authorization resources. */
  fun listResources(options: ListAuthorizationResourcesOptions? = null): AuthorizationResourceList {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListAuthorizationResourcesOptions()) as Map<String, String>

    return workos.get(
      "/authorization/resources",
      AuthorizationResourceList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }
}
