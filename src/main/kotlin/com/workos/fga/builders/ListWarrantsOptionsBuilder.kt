package com.workos.fga.builders

import com.workos.fga.types.ListWarrantsOptions

/**
 * Builder for options when listing warrants.
 *
 * @param resourceType Returns warrants whose resourceType matches this value. Required if resourceId or relation is specified.
 * @param resourceId Returns warrants whose resourceId matches this value.
 * @param relation Returns warrants whose relation matches this value.
 * @param subjectType Returns warrants with a subject whose resourceType matches this value.
 * @param subjectId Returns warrants with a subject whose resourceId matches this value.
 * @param subjectRelation Returns warrants with a subject whose relation matches this value.
 * @param limit Maximum number of records to return.
 * @param after Pagination cursor to receive records after a provided warrant ID.
 */
class ListWarrantsOptionsBuilder @JvmOverloads constructor(
  private var resourceType: String? = null,
  private var resourceId: String? = null,
  private var relation: String? = null,
  private var subjectType: String? = null,
  private var subjectId: String? = null,
  private var subjectRelation: String? = null,
  private var limit: Int? = null,
  private var after: String? = null,
) {
  /**
   * Resource Type
   */
  fun resourceType(value: String) = apply { resourceType = value }

  /**
   * Resource ID
   */
  fun resourceId(value: String) = apply { resourceId = value }

  /**
   * Resource Type
   */
  fun relation(value: String) = apply { relation = value }

  /**
   * Subject Type
   */
  fun subjectType(value: String) = apply { subjectType = value }

  /**
   * Subject ID
   */
  fun subjectId(value: String) = apply { subjectId = value }

  /**
   * Subject Relation
   */
  fun subjectRelation(value: String) = apply { subjectRelation = value }

  /**
   * Limit
   */
  fun limit(value: Int) = apply { limit = value }

  /**
   * After
   */
  fun after(value: String) = apply { after = value }

  /**
   * Generates the ListWarrants options.
   */
  fun build(): ListWarrantsOptions {
    return ListWarrantsOptions(
      resourceType = this.resourceType,
      resourceId = this.resourceId,
      relation = this.relation,
      subjectType = this.subjectType,
      subjectId = this.subjectId,
      subjectRelation = this.subjectRelation,
      limit = this.limit,
      after = this.after,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(): ListWarrantsOptionsBuilder {
      return ListWarrantsOptionsBuilder()
    }
  }
}
