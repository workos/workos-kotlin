package com.workos.usermanagement.builders

import com.workos.usermanagement.types.UpdateOrganizationMembershipOptions

/**
 * Builder for options when updating an organization membership.
 *
 * @param id The id of the organization membership.
 * @param roleSlug The unique role identifier.
 * @param roleSlugs The unique role identifiers for multiple roles support.
 */
class UpdateOrganizationMembershipOptionsBuilder @JvmOverloads constructor(
  private var id: String,
  private var roleSlug: String? = null,
  private var roleSlugs: List<String>? = null,
) {
  /**
   * Role Slug
   */
  fun roleSlug(value: String) = apply { roleSlug = value }

  /**
   * Role Slugs for multiple roles support
   */
  fun roleSlugs(value: List<String>) = apply { roleSlugs = value }

  /**
   * Generates the UpdateOrganizationMembershipOptions object.
   */
  fun build(): UpdateOrganizationMembershipOptions {
    return UpdateOrganizationMembershipOptions(
      id = this.id,
      roleSlug = this.roleSlug,
      roleSlugs = this.roleSlugs,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(id: String): UpdateOrganizationMembershipOptionsBuilder {
      return UpdateOrganizationMembershipOptionsBuilder(id)
    }

    @JvmStatic
    fun create(id: String, roleSlug: String): UpdateOrganizationMembershipOptionsBuilder {
      return UpdateOrganizationMembershipOptionsBuilder(id, roleSlug)
    }

    @JvmStatic
    fun create(id: String, roleSlugs: List<String>): UpdateOrganizationMembershipOptionsBuilder {
      return UpdateOrganizationMembershipOptionsBuilder(id, null, roleSlugs)
    }
  }
}
