// @oagen-ignore-file
@file:JvmName("IdentityTokenProviders")

package com.workos.workloadidentity

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

/**
 * Returns the workload's identity token: the JWT its platform issued, which
 * [WorkloadIdentityCredentials] exchanges for a WorkOS access token.
 *
 * Any lambda works, in Kotlin and in Java:
 *
 * ```kotlin
 * val provider = IdentityTokenProvider { fetchJwtFromMyPlatform() }
 * ```
 *
 * The provider is called once per exchange attempt. It should not cache
 * beyond what its platform already does, and it must not retry: the SDK
 * calls it again when an exchange attempt fails. Signal a failure by
 * throwing; the message must not contain the token.
 */
fun interface IdentityTokenProvider {
  /** Return the identity token. Throw when it cannot be obtained. */
  @Throws(Exception::class)
  fun getToken(): String
}

/**
 * An [IdentityTokenProvider] that reads the token from the file at [path] on
 * every call. The file is one that another process writes, such as a sidecar
 * or an earlier CI step that fetches a new token, so a new token is picked
 * up. Surrounding whitespace is trimmed. A missing or empty file is an error.
 */
fun tokenFile(path: String): IdentityTokenProvider =
  IdentityTokenProvider {
    val token =
      try {
        Files.readString(Paths.get(path)).trim()
      } catch (e: NoSuchFileException) {
        throw IllegalStateException("The identity token file $path does not exist.", e)
      }
    check(token.isNotEmpty()) { "The identity token file $path is empty." }
    token
  }

/**
 * An [IdentityTokenProvider] that reads the token from the environment
 * variable [name] on every call. An unset or empty variable is an error.
 */
fun tokenEnv(name: String): IdentityTokenProvider = tokenEnv(name) { System.getenv(it) }

/** [tokenEnv] with the environment lookup supplied by the caller, for tests. Hidden from Java. */
@JvmSynthetic
internal fun tokenEnv(
  name: String,
  env: (String) -> String?
): IdentityTokenProvider =
  IdentityTokenProvider {
    val token = env(name)
    check(!token.isNullOrEmpty()) { "The environment variable $name is not set or is empty." }
    token
  }
