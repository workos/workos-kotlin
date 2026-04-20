// @oagen-ignore-file
package com.workos.common.http

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.workos.common.exceptions.BadRequestException
import com.workos.common.exceptions.GenericException
import com.workos.common.exceptions.GenericServerException
import com.workos.common.exceptions.NotFoundException
import com.workos.common.exceptions.RateLimitException
import com.workos.common.exceptions.UnauthorizedException
import com.workos.common.exceptions.UnprocessableEntityException
import com.workos.common.exceptions.WorkOSException
import com.workos.common.json.ObjectMapperFactory
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

private val FORM_MEDIA_TYPE = "application/x-www-form-urlencoded".toMediaType()

/**
 * Low-level HTTP client used by every generated service.
 *
 * Handles:
 *  - URL and header assembly
 *  - JSON serialization (Jackson)
 *  - retry policy (exponential backoff with jitter, honoring `Retry-After`)
 *  - automatic idempotency keys for retried POSTs that lacked one
 *  - per-request option honoring (headers, base URL, timeout, maxRetries, idempotencyKey, apiKey)
 *  - response deserialization and error translation into [WorkOSException] subclasses
 */
open class BaseClient(
  @JvmField val apiKey: String,
  @JvmField val apiBaseUrl: String,
  private val httpClient: OkHttpClient,
  @JvmField val objectMapper: ObjectMapper = ObjectMapperFactory.create(),
  @JvmField val retryConfig: RetryConfig = RetryConfig.DEFAULT,
  @JvmField val userAgent: String = "workos-kotlin"
) {
  private val retryPolicy = RetryPolicy(retryConfig)

  /** Execute a request and deserialize the response as [T]. */
  fun <T> request(
    config: RequestConfig,
    responseType: Class<T>
  ): T {
    val (_, body) = execute(config)
    return objectMapper.readValue(body, responseType)
  }

  /** Execute a request and deserialize using a Jackson [TypeReference] (for generics). */
  fun <T> request(
    config: RequestConfig,
    responseType: TypeReference<T>
  ): T {
    val (_, body) = execute(config)
    return objectMapper.readValue(body, responseType)
  }

  /** Execute a request and return the parsed JSON as a [JsonNode]. */
  fun requestJson(config: RequestConfig): JsonNode {
    val (_, body) = execute(config)
    return objectMapper.readTree(body)
  }

  /** Execute a request discarding the response body (for DELETE / 204 responses). */
  fun requestVoid(config: RequestConfig) {
    execute(config)
  }

  /**
   * Execute a paginated list request. The emitted service code passes in the
   * item [TypeReference] and a builder that reconstructs the [RequestConfig]
   * for the next page given a cursor, so [Page.autoPagingIterable] can walk
   * subsequent pages.
   */
  fun <T> requestPage(
    config: RequestConfig,
    itemType: TypeReference<T>,
    nextPageConfig: (after: String) -> RequestConfig
  ): Page<T> {
    val (_, body) = execute(config)
    val tree = objectMapper.readTree(body)
    val items =
      tree.path("data").let { dataNode ->
        if (dataNode.isMissingNode || dataNode.isNull) {
          emptyList()
        } else {
          val collectionType =
            objectMapper.typeFactory.constructCollectionType(
              ArrayList::class.java,
              objectMapper.typeFactory.constructType(itemType)
            )
          objectMapper.convertValue<ArrayList<T>>(dataNode, collectionType)
        }
      }
    val metadata = parseListMetadata(tree)
    val fetcher: (String) -> Page<T> = { after ->
      requestPage(nextPageConfig(after), itemType, nextPageConfig)
    }
    return Page(items, metadata, fetcher)
  }

  /**
   * Execute a paginated request while keeping the cursor plumbing in one
   * place. Generated service methods only need to contribute the stable
   * query parameters for the first page.
   */
  @JvmOverloads
  fun <T> requestPage(
    method: String,
    path: String,
    itemType: TypeReference<T>,
    requestOptions: RequestOptions? = null,
    before: String? = null,
    after: String? = null,
    buildQueryParams: MutableList<Pair<String, String>>.() -> Unit = {}
  ): Page<T> {
    fun configFor(afterCursor: String? = null): RequestConfig {
      val params = mutableListOf<Pair<String, String>>()
      params.buildQueryParams()
      val effectiveAfter = afterCursor ?: after
      if (effectiveAfter == null && before != null) {
        params += "before" to before
      }
      if (effectiveAfter != null) {
        params += "after" to effectiveAfter
      }
      return RequestConfig(
        method = method,
        path = path,
        queryParams = params,
        requestOptions = requestOptions
      )
    }

    return requestPage(configFor(), itemType) { cursor -> configFor(cursor) }
  }

  /**
   * Execute a request and return `(status, responseBody)`. Retries transport
   * errors and 429/5xx responses per [RetryPolicy].
   */
  private fun execute(config: RequestConfig): Pair<Int, String> {
    val maxRetries = config.requestOptions?.maxRetries ?: retryConfig.maxRetries
    val needsAutoIdempotency =
      config.method.equals("POST", ignoreCase = true) &&
        config.requestOptions?.idempotencyKey == null &&
        maxRetries > 0

    val effectiveIdempotencyKey =
      config.requestOptions?.idempotencyKey
        ?: if (needsAutoIdempotency) retryPolicy.generateIdempotencyKey(buildRetrySeed(config)) else null

    var attempt = 0
    val requestStartedAt = System.nanoTime()
    val timeoutBudgetMs = config.requestOptions?.timeoutMillis
    while (true) {
      val request = buildRequest(config, effectiveIdempotencyKey)
      val effectiveClient = applyPerRequestTimeout(config.requestOptions)
      val outcome: AttemptOutcome
      val response: Response?
      try {
        response = effectiveClient.newCall(request).execute()
        outcome =
          AttemptOutcome.Response(
            statusCode = response.code,
            retryAfter = response.header("Retry-After")
          )
      } catch (e: IOException) {
        val delay = retryPolicy.nextDelay(attempt, AttemptOutcome.TransportFailure(e), maxRetries)
        if (delay == null) throw translateTransportFailure(e)
        sleep(coerceDelay(delay, timeoutBudgetMs, requestStartedAt) ?: throw translateTransportFailure(e))
        attempt += 1
        continue
      }

      response.use { resp ->
        val status = resp.code
        val bodyString = resp.body?.string().orEmpty()
        val requestId = resp.header("X-Request-Id") ?: resp.header("X-Request-ID")
        val retryAfterHeader = resp.header("Retry-After")

        if (status in 200..299) {
          return status to bodyString
        }

        val delay = retryPolicy.nextDelay(attempt, outcome, maxRetries)
        if (delay == null) {
          throw translateHttpFailure(status, requestId, bodyString, request.url.toString(), retryAfterHeader)
        }
        sleep(
          coerceDelay(delay, timeoutBudgetMs, requestStartedAt)
            ?: throw translateHttpFailure(status, requestId, bodyString, request.url.toString(), retryAfterHeader)
        )
      }
      attempt += 1
    }
  }

  private fun buildRequest(
    config: RequestConfig,
    idempotencyKey: String?
  ): Request {
    val effectiveBase = config.requestOptions?.baseUrl ?: apiBaseUrl
    val urlBuilder =
      effectiveBase
        .trimEnd('/')
        .let { "$it${config.path}" }
        .toHttpUrl()
        .newBuilder()
    for ((name, value) in config.queryParams) {
      urlBuilder.addQueryParameter(name, value)
    }

    val okBody =
      when {
        config.formBody != null -> {
          val encoded =
            config.formBody.entries.joinToString("&") { (k, v) ->
              "${urlEncode(k)}=${urlEncode(v)}"
            }
          encoded.toRequestBody(FORM_MEDIA_TYPE)
        }
        config.body == null -> null
        config.body is String -> (config.body as String).toRequestBody(JSON_MEDIA_TYPE)
        config.body is ByteArray -> (config.body as ByteArray).toRequestBody(JSON_MEDIA_TYPE)
        else -> objectMapper.writeValueAsBytes(config.body).toRequestBody(JSON_MEDIA_TYPE)
      }

    val builder =
      Request
        .Builder()
        .url(urlBuilder.build())
        .method(config.method.uppercase(), okBody)

    // Authorization: bearer override (per-op token) > per-request apiKey > client apiKey
    val effectiveApiKey =
      config.accessToken
        ?: config.requestOptions?.apiKey
        ?: apiKey
    builder.header("Authorization", "Bearer $effectiveApiKey")
    builder.header("User-Agent", userAgent)
    builder.header("Accept", "application/json")
    if (idempotencyKey != null) {
      builder.header("Idempotency-Key", idempotencyKey)
    }
    config.requestOptions?.additionalHeaders?.forEach { (k, v) -> builder.header(k, v) }
    return builder.build()
  }

  private fun applyPerRequestTimeout(options: RequestOptions?): OkHttpClient {
    val timeoutMillis = options?.timeoutMillis ?: return httpClient
    return httpClient
      .newBuilder()
      .callTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
      .build()
  }

  /** Hook for tests to suppress real sleeps. Visible for testing subclasses only. */
  protected open fun sleep(millis: Long) {
    if (millis > 0) Thread.sleep(millis)
  }

  private fun parseListMetadata(tree: JsonNode): ListMetadata {
    val node = tree.path("list_metadata").takeUnless { it.isMissingNode || it.isNull } ?: tree.path("listMetadata")
    return ListMetadata(
      before = node.path("before").takeIf { !it.isMissingNode && !it.isNull && it.isTextual }?.asText(),
      after = node.path("after").takeIf { !it.isMissingNode && !it.isNull && it.isTextual }?.asText()
    )
  }

  private fun translateTransportFailure(e: IOException): WorkOSException =
    when (e) {
      is SocketTimeoutException ->
        GenericException(0, null, "timeout", "Request timed out: ${e.message}", null, e)
      else ->
        GenericException(0, null, "io_error", e.message ?: "I/O error", null, e)
    }

  private fun translateHttpFailure(
    status: Int,
    requestId: String?,
    body: String,
    url: String,
    retryAfterHeader: String? = null
  ): WorkOSException {
    val (code, message, errors) = parseApiError(body)
    return when (status) {
      400 -> BadRequestException(requestId, code, message, errors, body)
      401 -> UnauthorizedException(requestId, code, message, body)
      404 -> NotFoundException(requestId, code, message, url, body)
      422 -> UnprocessableEntityException(requestId, code, message, errors, body)
      429 -> RateLimitException(requestId, code, message, retryAfterHeader?.let { RetryPolicy.parseRetryAfter(it)?.div(1000L) }, body)
      in 500..599 -> GenericServerException(status, requestId, code, message, body)
      else -> GenericException(status, requestId, code, message, body)
    }
  }

  private fun parseApiError(body: String): Triple<String?, String?, List<Map<String, Any?>>?> {
    if (body.isBlank()) return Triple(null, null, null)
    return try {
      val tree = objectMapper.readTree(body)
      val code = tree.path("code").takeIf { it.isTextual }?.asText()
      val message =
        tree.path("message").takeIf { it.isTextual }?.asText()
          ?: tree.path("error_description").takeIf { it.isTextual }?.asText()
      val errorsNode = tree.path("errors")
      val errors =
        if (errorsNode.isArray) {
          val typeRef = object : TypeReference<List<Map<String, Any?>>>() {}
          objectMapper.convertValue(errorsNode, typeRef)
        } else {
          null
        }
      Triple(code, message, errors)
    } catch (_: JsonMappingException) {
      Triple(null, null, null)
    } catch (_: IOException) {
      Triple(null, null, null)
    }
  }

  private fun urlEncode(value: String): String = java.net.URLEncoder.encode(value, Charsets.UTF_8)

  private fun buildRetrySeed(config: RequestConfig): String {
    val querySeed = config.queryParams.joinToString("&") { (key, value) -> "$key=$value" }
    val formSeed =
      config.formBody
        ?.entries
        ?.joinToString("&") { (key, value) -> "$key=$value" }
        .orEmpty()
    val bodySeed =
      when (val body = config.body) {
        null -> ""
        is String -> body
        is ByteArray -> body.decodeToString()
        else -> objectMapper.writeValueAsString(body)
      }
    return "${config.method.uppercase()}|${config.path}|$querySeed|$formSeed|$bodySeed"
  }

  private fun coerceDelay(
    delayMs: Long,
    timeoutBudgetMs: Long?,
    requestStartedAt: Long
  ): Long? {
    if (timeoutBudgetMs == null) return delayMs
    val elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartedAt)
    val remainingMs = timeoutBudgetMs - elapsedMs
    if (remainingMs <= 0) return null
    return delayMs.coerceAtMost(remainingMs)
  }
}
