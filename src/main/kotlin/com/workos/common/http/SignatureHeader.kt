// @oagen-ignore-file
package com.workos.common.http

import java.security.SignatureException

internal data class SignatureHeader(
  val timestamp: String,
  val signature: String
)

internal fun parseSignatureHeader(
  signatureHeader: String,
  signatureKeys: Set<String>
): SignatureHeader {
  val parts =
    signatureHeader
      .split(",")
      .mapNotNull { part ->
        val trimmed = part.trim()
        val index = trimmed.indexOf('=')
        if (index <= 0 || index >= trimmed.lastIndex) null else trimmed.substring(0, index) to trimmed.substring(index + 1)
      }.toMap(LinkedHashMap())

  val timestamp = parts["t"].orEmpty()
  val signature = signatureKeys.firstNotNullOfOrNull { key -> parts[key] }?.takeIf { it.isNotEmpty() }.orEmpty()
  if (timestamp.isEmpty() || signature.isEmpty()) {
    throw SignatureException("Missing timestamp or signature component in WorkOS-Signature header")
  }

  return SignatureHeader(timestamp = timestamp, signature = signature)
}
