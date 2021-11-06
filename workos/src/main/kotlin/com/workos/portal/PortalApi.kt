package com.workos.portal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.portal.models.Intent
import com.workos.portal.models.Link
import java.lang.IllegalArgumentException

/**
 * The PortalApi class provides convenience methods for working with the WorkOS
 * Admin Portal product.
 */
class PortalApi(private val workos: WorkOS) {

  /**
   * Parameters for [generateLink].
   *
   * @param organization Unique identifier for an [com.workos.organizations.models.Organization]
   * @param intent The type of setup to generate an Admin Portal link.
   * @param returnUrl The URL to which WorkOS should send users when they click on the link to return to your website.
   */
  @JsonInclude(Include.NON_NULL)
  class GeneratePortalLinkOptions @JvmOverloads constructor(
    val organization: String,

    val intent: Intent,

    @JsonProperty("return_url")
    val returnUrl: String? = null,
  ) {
    /**
     * Builder class for [GeneratePortalLinkOptions].
     */
    class GeneratePortalLinkOptionsBuilder {
      private var organization: String? = null

      private var intent: Intent? = null

      private var returnUrl: String? = null

      /**
       * Sets the organizationId.
       */
      fun organization(value: String) = apply { organization = value }

      /**
       * Sets the portal intent.
       */
      fun intent(value: Intent) = apply { intent = value }

      /**
       * Sets the return URL.
       */
      fun returnUrl(value: String) = apply { returnUrl = value }

      /**
       * Creates a [GeneratePortalLinkOptions] with the given builder parameters.
       */
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

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): GeneratePortalLinkOptionsBuilder {
        return GeneratePortalLinkOptionsBuilder()
      }
    }
  }

  /**
   * Generate an Admin Portal Link.
   */
  fun generateLink(generateLinkOptions: GeneratePortalLinkOptions): Link {
    val config = RequestConfig
      .builder()
      .data(generateLinkOptions)
      .build()

    return workos.post("/portal/generate_link", Link::class.java, config)
  }
}
