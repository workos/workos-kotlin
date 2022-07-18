package com.workos.mfa

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.mfa.models.Challenge
import com.workos.mfa.models.Factor
import com.workos.mfa.models.VerifyChallengeResponse
import com.workos.mfa.models.VerifyFactorResponse

class MfaApi(private val workos: WorkOS) {

  /**
   * Parameters for the [enrollFactor] method.
   */
  @JsonInclude(Include.NON_NULL)
  class EnrollFactorOptions @JvmOverloads constructor(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("totp_issuer")
    val issuer: String? = null,

    @JsonProperty("totp_user")
    val user: String? = null,

    @JsonProperty("phone_number")
    val phoneNumber: String? = null,
  ) {
    /**
     * Builder class for [enrollFactorOptions].
     */
    class EnrollFactorOptionsBuilder {
      private var type: String? = null

      private var issuer: String? = null

      private var user: String? = null

      private var phoneNumber: String? = null

      /**
       * Sets the type.
       */
      fun type(value: String) = apply { type = value }

      /**
       * Sets the totp issuer.
       */
      fun issuer(value: String) = apply { issuer = value }

      /**
       * Sets the totp user.
       */
      fun user(value: String) = apply { user = value }

      /**
       * Sets the totp user.
       */
      fun phoneNumber(value: String) = apply { phoneNumber = value }

      /**
       * Creates a [EnrollFactorOptions] with the given builder parameters.
       */
      fun build(): EnrollFactorOptions {
        if (type == null || (type != "generic_otp" && type != "totp" && type != "sms")) {
          throw IllegalArgumentException("The mfa type must be either generic_otp, totp, or sms")
        }

        if (type == "totp") {
          if (issuer == null || user == null) {
            throw IllegalArgumentException("Type totp must have an issuer and user")
          }
        }

        if (type == "sms") {
          if (phoneNumber == null) {
            throw IllegalArgumentException("Type sms type must have a phone number")
          }
        }

        return EnrollFactorOptions(
          type = type!!,
          issuer = issuer,
          user = user,
          phoneNumber = phoneNumber
        )
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): EnrollFactorOptionsBuilder {
        return EnrollFactorOptionsBuilder()
      }
    }
  }

  /**
   * Enrolls a Factor.
   */
  fun enrollFactor(enrollFactorOptions: EnrollFactorOptions): Factor {
    val config = RequestConfig.builder()
      .data(enrollFactorOptions)
      .build()

    return workos.post("/auth/factors/enroll", Factor::class.java, config)
  }

  /**
   * Parameters for the [challengeFactor] method.
   */
  @JsonInclude(Include.NON_NULL)
  class ChallengeFactorOptions @JvmOverloads constructor(
    @JsonProperty("authentication_factor_id")
    val authenticationFactorId: String,

    @JsonProperty("sms_template")
    val smsTemplate: String? = null,
  ) {
    /**
     * Builder class for [ChalllengeFactorOptions].
     */
    class ChallengeFactorOptionsBuilder {
      private var authenticationFactorId: String? = null

      private var smsTemplate: String? = null

      /**
       * Sets the auth factor ID.
       */
      fun authenticationFactorId(value: String) = apply { authenticationFactorId = value }

      /**
       * Sets sms template.
       */
      fun smsTemplate(value: String) = apply { smsTemplate = value }

      /**
       * Creates a [ChallengeFactorOptions] with the given builder parameters.
       */
      fun build(): ChallengeFactorOptions {
        if (authenticationFactorId == null) {
          throw IllegalArgumentException("Must provide an authentication factor ID")
        }

        return ChallengeFactorOptions(
          authenticationFactorId = authenticationFactorId!!,
          smsTemplate = smsTemplate,
        )
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): ChallengeFactorOptionsBuilder {
        return ChallengeFactorOptionsBuilder()
      }
    }
  }

  /**
   * Challenges a Factor.
   */
  fun challengeFactor(challengeFactorOptions: ChallengeFactorOptions): Challenge {
    val config = RequestConfig.builder()
      .data(challengeFactorOptions)
      .build()

    return workos.post("/auth/factors/${challengeFactorOptions.authenticationFactorId}/challenge", Challenge::class.java, config)
  }

  /**
   * Parameters for the [verifyFactor] method.
   */
  @Deprecated("Please use `verifyChallenge` instead")
  @JsonInclude(Include.NON_NULL)
  class VerifyFactorOptions constructor(
    @JsonProperty("authentication_challenge_id")
    val authenticationChallengeId: String,

    @JsonProperty("code")
    val code: String,
  ) {
    /**
     * Builder class for [ChalllengeFactorOptions].
     */
    class VerifyFactorOptionsBuilder {
      private var authenticationChallengeId: String? = null

      private var code: String? = null

      /**
       * Sets the auth factor ID.
       */
      fun authenticationChallengeId(value: String) = apply { authenticationChallengeId = value }

      /**
       * Sets sms template.
       */
      fun code(value: String) = apply { code = value }

      /**
       * Creates a [ChallengeFactorOptions] with the given builder parameters.
       */
      fun build(): VerifyFactorOptions {
        if (authenticationChallengeId == null) {
          throw IllegalArgumentException("Must provide a challenge factor ID")
        }

        if (code == null) {
          throw IllegalArgumentException("Must provide an mfa code")
        }

        return VerifyFactorOptions(
          authenticationChallengeId = authenticationChallengeId!!,
          code = code!!
        )
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): VerifyFactorOptionsBuilder {
        return VerifyFactorOptionsBuilder()
      }
    }
  }

  /**
   * Verifies a Factor.
   */
  @Deprecated("Please use `verifyChallenge` instead")
  fun verifyFactor(verifyFactorOptions: VerifyFactorOptions): VerifyFactorResponse {
    val config = RequestConfig.builder()
      .data(verifyFactorOptions)
      .build()

    return workos.post("/auth/factors/verify", VerifyFactorResponse::class.java, config)
  }

  /**
   * Parameters for the [verifyChallenge] method.
   */
  @JsonInclude(Include.NON_NULL)
  class VerifyChallengeOptions constructor(
    @JsonProperty("authentication_challenge_id")
    val authenticationChallengeId: String,

    @JsonProperty("code")
    val code: String,
  ) {
    /**
     * Builder class for [VerifyChalllengeOptions].
     */
    class VerifyChallengeOptionsBuilder {
      private var authenticationChallengeId: String? = null

      private var code: String? = null

      /**
       * Sets the auth factor ID.
       */
      fun authenticationChallengeId(value: String) = apply { authenticationChallengeId = value }

      /**
       * Sets sms template.
       */
      fun code(value: String) = apply { code = value }

      /**
       * Creates a [VerifyChallengeOptions] with the given builder parameters.
       */
      fun build(): VerifyChallengeOptions {
        if (authenticationChallengeId == null) {
          throw IllegalArgumentException("Must provide a challenge factor ID")
        }

        if (code == null) {
          throw IllegalArgumentException("Must provide an mfa code")
        }

        return VerifyChallengeOptions(
          authenticationChallengeId = authenticationChallengeId!!,
          code = code!!
        )
      }
    }

    /**
     * @suppress
     */
    companion object {
      @JvmStatic
      fun builder(): VerifyChallengeOptionsBuilder {
        return VerifyChallengeOptionsBuilder()
      }
    }
  }

  /**
   * Verifies a Challenge
   */
  fun verifyChallenge(verifyChallengeOptions: VerifyChallengeOptions): VerifyChallengeResponse {
    val config = RequestConfig.builder()
      .data(verifyChallengeOptions)
      .build()

    return workos.post("/auth/challenges/${verifyChallengeOptions.authenticationChallengeId}/verify", VerifyChallengeResponse::class.java, config)
  }

  /**
   * Gets a Factor by id.
   */
  fun getFactor(id: String): Factor {
    return workos.get("/auth/factors/$id", Factor::class.java)
  }

  /**
   * Deletes a Factor by id.
   */
  fun deleteFactor(id: String) {
    workos.delete("/auth/factors/$id")
  }
}
