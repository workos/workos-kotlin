package com.workos.widgets

import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.widgets.models.WidgetToken
import com.workos.widgets.types.GetTokenOptions

/**
 * The WidgetsApi class provides convenience methods for working with the WorkOS
 * Widgets product.
 */
class WidgetsApi(private val workos: WorkOS) {
  /**
   * Generates a widget token.
   */
  fun getToken(options: GetTokenOptions): WidgetToken {
    return workos.post(
      "/widgets/token",
      WidgetToken::class.java,
      RequestConfig.builder().data(options).build()
    )
  }
}
