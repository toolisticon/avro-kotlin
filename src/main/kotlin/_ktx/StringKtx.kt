package _ktx

import java.io.File

object StringKtx {
  fun String?.trimToNull() = if (this.isNullOrBlank()) null else this

  fun <K : Any, V : Any> Map<K, V>.toReadableString() = StringBuilder()
    .apply {
      this@toReadableString.forEach { (k, v) ->
        appendLine("$k:")
        appendLine("\t$v")
        appendLine()
      }
    }
    .toString()

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

  fun String.trailingSlash() = if (this.startsWith("/")) this else "/$this"

  fun csv(vararg values: String?) = listOfNotNull(*values).joinToString(separator = ", ")

  fun String.removeSeparatorPrefix() = removePrefix(File.separator)
}
