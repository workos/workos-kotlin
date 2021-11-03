package com.workos.portal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.portal.models.Intent
import com.workos.portal.models.Link
import java.lang.IllegalArgumentException

class PortalApi(val workos: WorkOS) {

  @JsonInclude(Include.NON_NULL)
  class GeneratePortalLinkOptions @JvmOverloads constructor(
    val organization: String,

    val intent: Intent,

    @JsonProperty("return_url")
    val returnUrl: String? = null,
  ) {
    class Builder {
      private var organization: String? = null

      private var intent: Intent? = null

      private var returnUrl: String? = null

      fun organization(value: String) = apply { organization = value }

      fun intent(value: Intent) = apply { intent = value }

      fun returnUrl(value: String) = apply { returnUrl = value }

      fun build(): GeneratePortalLinkOptions {
        if (organization == null) {
          throw IllegalArgumentException("An organization id must be provided")
        }

        if (intent == null) {
          throw IllegalArgumentException("An intent must be provided")
        }

        return GeneratePortalLinkOptions(
          organization = organization!!,
          intent = intent!!,
          returnUrl = returnUrl
        )
      }
    }

    companion object {
      @JvmStatic
      fun builder(): Builder {
        return Builder()
      }
    }
  }

  fun generateLink(generateLinkOptions: GeneratePortalLinkOptions): Link {
    val config = RequestConfig
      .builder()
      .data(generateLinkOptions)
      .build()

    return workos.post("/portal/generate_link", Link::class.java, config)
  }
}
