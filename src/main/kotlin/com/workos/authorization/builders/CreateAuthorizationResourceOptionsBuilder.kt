package com.workos.authorization.builders

import com.workos.authorization.types.CreateAuthorizationResourceOptions

class CreateAuthorizationResourceOptionsBuilder @JvmOverloads constructor(
  private var organizationId: String,
  private var resourceTypeSlug: String,
  private var externalId: String,
  private var name: String,
  private var description: String? = null,
  private var parentResourceId: String? = null,
  private var parentResourceExternalId: String? = null,
  private var parentResourceTypeSlug: String? = null
) {
  fun organizationId(value: String) = apply { organizationId = value }

  fun resourceTypeSlug(value: String) = apply { resourceTypeSlug = value }

  fun externalId(value: String) = apply { externalId = value }

  fun name(value: String) = apply { name = value }

  fun description(value: String) = apply { description = value }

  fun parentResourceId(value: String) = apply { parentResourceId = value }

  fun parentResourceExternalId(value: String) = apply { parentResourceExternalId = value }

  fun parentResourceTypeSlug(value: String) = apply { parentResourceTypeSlug = value }

  fun build(): CreateAuthorizationResourceOptions {
    return CreateAuthorizationResourceOptions(
      organizationId = this.organizationId,
      resourceTypeSlug = this.resourceTypeSlug,
      externalId = this.externalId,
      name = this.name,
      description = this.description,
      parentResourceId = this.parentResourceId,
      parentResourceExternalId = this.parentResourceExternalId,
      parentResourceTypeSlug = this.parentResourceTypeSlug
    )
  }

  companion object {
    @JvmStatic
    fun create(organizationId: String, resourceTypeSlug: String, externalId: String, name: String): CreateAuthorizationResourceOptionsBuilder {
      return CreateAuthorizationResourceOptionsBuilder(organizationId, resourceTypeSlug, externalId, name)
    }
  }
}
