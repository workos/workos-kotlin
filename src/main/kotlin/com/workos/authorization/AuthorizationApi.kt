package com.workos.authorization

import com.workos.WorkOS
import com.workos.authorization.models.AuthorizationCheckResult
import com.workos.authorization.models.AuthorizationResource
import com.workos.authorization.models.AuthorizationResourceList
import com.workos.authorization.types.CheckAuthorizationOptions
import com.workos.authorization.types.CreateAuthorizationResourceOptions
import com.workos.authorization.types.ListAuthorizationResourcesOptions
import com.workos.authorization.types.UpdateAuthorizationResourceOptions
import com.workos.common.http.RequestConfig

class AuthorizationApi(private val workos: WorkOS) {

  companion object {
    internal const val RESOURCES_PATH = "/authorization/resources"
<<<<<<< HEAD
=======
    internal const val ORGANIZATIONS_PATH = "/authorization/organizations"
>>>>>>> 7a820c1 (formatting, breaking up paths to common strings, add inner classes for tests)
    internal const val ORGANIZATION_MEMBERSHIPS_PATH = "/authorization/organization_memberships"
  }

  /** Get a resource by internal ID. */
  fun getResource(resourceId: String): AuthorizationResource {
    return workos.get(
      "$RESOURCES_PATH/$resourceId",
      AuthorizationResource::class.java
    )
  }

  /** Create a new authorization resource. */
  fun createResource(options: CreateAuthorizationResourceOptions): AuthorizationResource {
    return workos.post(
      RESOURCES_PATH,
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Update a resource by internal ID. */
  fun updateResource(resourceId: String, options: UpdateAuthorizationResourceOptions): AuthorizationResource {
    return workos.patch(
      "$RESOURCES_PATH/$resourceId",
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
    workos.delete("$RESOURCES_PATH/$resourceId", config)
  }

  /** List authorization resources. */
  fun listResources(options: ListAuthorizationResourcesOptions? = null): AuthorizationResourceList {
    val params: Map<String, String> =
      RequestConfig.toMap(options ?: ListAuthorizationResourcesOptions()) as Map<String, String>

    return workos.get(
      RESOURCES_PATH,
      AuthorizationResourceList::class.java,
      RequestConfig.builder().params(params).build()
    )
  }

  /** Get a resource by external ID. */
  fun getResourceByExternalId(organizationId: String, resourceTypeSlug: String, externalId: String): AuthorizationResource {
    return workos.get(
      "$ORGANIZATIONS_PATH/$organizationId/resources/$resourceTypeSlug/$externalId",
      AuthorizationResource::class.java
    )
  }

  /** Update a resource by external ID. */
  fun updateResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    options: UpdateAuthorizationResourceOptions
  ): AuthorizationResource {
    return workos.patch(
      "$ORGANIZATIONS_PATH/$organizationId/resources/$resourceTypeSlug/$externalId",
      AuthorizationResource::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /** Delete a resource by external ID. */
  fun deleteResourceByExternalId(
    organizationId: String,
    resourceTypeSlug: String,
    externalId: String,
    cascadeDelete: Boolean = false
  ) {
    val config = if (cascadeDelete) {
      RequestConfig.builder().params(mapOf("cascade_delete" to "true")).build()
    } else {
      null
    }
    workos.delete("$ORGANIZATIONS_PATH/$organizationId/resources/$resourceTypeSlug/$externalId", config)
  }

  /** Check authorization for an organization membership. */
  fun check(organizationMembershipId: String, options: CheckAuthorizationOptions): AuthorizationCheckResult {
    return workos.post(
      "$ORGANIZATION_MEMBERSHIPS_PATH/$organizationMembershipId/check",
      AuthorizationCheckResult::class.java,
      RequestConfig.builder().data(options).build()
    )
  }
}
