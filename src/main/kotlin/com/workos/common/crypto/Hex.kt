// @oagen-ignore-file
package com.workos.common.crypto

private val HEX_DIGITS = "0123456789abcdef".toCharArray()

internal fun ByteArray.toHex(): String {
  val chars = CharArray(size * 2)
  forEachIndexed { index, byte ->
    val value = byte.toInt() and 0xff
    chars[index * 2] = HEX_DIGITS[value ushr 4]
    chars[index * 2 + 1] = HEX_DIGITS[value and 0x0f]
  }
  return String(chars)
}

internal fun String.decodeHexOrNull(): ByteArray? {
  val normalized = trim()
  if (normalized.length % 2 != 0) return null
  val bytes = ByteArray(normalized.length / 2)
  var i = 0
  while (i < normalized.length) {
    val high = Character.digit(normalized[i], 16)
    val low = Character.digit(normalized[i + 1], 16)
    if (high < 0 || low < 0) return null
    bytes[i / 2] = ((high shl 4) + low).toByte()
    i += 2
  }
  return bytes
}
