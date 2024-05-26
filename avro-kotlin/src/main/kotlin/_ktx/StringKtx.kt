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

  fun toString(name: String, block: ToStringBuilder.() -> Unit = {}) = ToStringBuilder(name).apply(block).toString()

  @JvmInline
  value class ToStringBuilder private constructor(private val pair: Pair<String, MutableMap<String, String>>) {
    companion object {
      val isNotNull: (Any?) -> Boolean = { it != null }

      internal fun String.shorten(maxLength: Int?, append: String = "...") = if (maxLength != null && length > maxLength)
        this.take(maxLength) + append
      else this
    }

    constructor(name: String) : this(name to mutableMapOf())

    private fun add(property: String, value: String) = apply { pair.second[property] = value }

    fun addIfNotNull(
      property: String,
      value: Any?,
      wrap: String = ""
    ) = addIf(property, value, wrap, isNotNull)

    fun addIfNotEmpty(
      property: String,
      value: Collection<*>?
    ) = addIf(property = property, value = value, wrap = "") { !value.isNullOrEmpty() }

    fun addIfNotEmpty(
      property: String,
      value: Map<*, *>?
    ) = addIf(property = property, value = value) { !value.isNullOrEmpty() }


    fun <T> addIf(
      property: String,
      value: T?,
      wrap: String = "",
      condition: (T?) -> Boolean = { true }
    ) = apply {
      if (condition(value)) {
        add(property, "$wrap$value$wrap")
      }
    }

    fun add(
      property: String,
      value: Any,
      wrap: String = "",
      maxLength: Int = Int.MAX_VALUE,
    ) = add(property, "$wrap${value.toString().shorten(maxLength)}$wrap")

    override fun toString(): String = "${pair.first}(${pair.second.map { (k, v) -> "$k=$v" }.joinToString(", ")})"
  }
}
