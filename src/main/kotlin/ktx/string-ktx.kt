@file: JvmName("StringKtx")

package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.AvroKotlin
import java.io.File

fun String?.trimToNull() = if (this.isNullOrBlank()) null else this

fun String.withNamespace(namespace: String?): String = if (this.contains(".") || namespace == null) {
  this
} else {
  "$namespace.$this"
}

fun ifTrue(condition: Boolean, caseTrue: String, caseFalse: String = ""): String = if (condition)
  caseTrue
else
  caseFalse


fun String.shorten(maxLength: Int = 100, append: String = "...") = if (length > maxLength)
  this.take(maxLength) + append
else this

fun Any?.nullableToString(
  prefix: String = "",
  suffix: String = "",
  maxLength: Int = Int.MAX_VALUE,
  append: String = ""
) = if (this != null)
  "$prefix${this.toString().shorten(maxLength, append)}$suffix"
else
  ""

fun String.firstUppercase() = this.replaceFirstChar(Char::uppercase)

/**
 * Replace namespace dots with file-separator dash.
 */
fun String.dotToDash(): String = replace(AvroKotlin.Constants.NAME_SEPARATOR, File.separator)

fun String.trailingSlash() = if (this.startsWith("/")) this else "/$this"
