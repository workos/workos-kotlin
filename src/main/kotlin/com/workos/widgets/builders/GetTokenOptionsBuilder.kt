package com.workos.widgets.builders

import com.workos.widgets.models.WidgetScope
import com.workos.widgets.types.GetTokenOptions

/**
 * Builder for options when generating a widget token.
 *
 * @param organizationId The ID of the organization to generate a token for.
 * @param userId The ID of the user to generate a token for.
 * @param scopes The scopes to generate a token for.
 */
class GetTokenOptionsBuilder @JvmOverloads constructor(
  private var organizationId: String,
  private var userId: String,
  private var scopes: List<WidgetScope>,
) {
  /**
   * Organization ID
   */
  fun organizationId(value: String) = apply { organizationId = value }

  /**
   * User ID
   */
  fun userId(value: String) = apply { userId = value }

  /**
   * Widget scopes
   */
  fun scopes(value: List<WidgetScope>) = apply { scopes = value }

  /**
   * Generates the GetTokenOptions object.
   */
  fun build(): GetTokenOptions {
    return GetTokenOptions(
      organizationId = this.organizationId,
      userId = this.userId,
      scopes = this.scopes,
    )
  }

  /**
   * @suppress
   */
  companion object {
    @JvmStatic
    fun create(organizationId: String, userId: String, scopes: List<WidgetScope>): GetTokenOptionsBuilder {
      return GetTokenOptionsBuilder(organizationId, userId, scopes)
    }
  }
}
