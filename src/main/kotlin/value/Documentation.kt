package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.nullableToString


/**
 * The documentation of a [Schema.Field] as defined in the `doc` element.
 */
@JvmInline
value class Documentation(override val value: String) : ValueType<String> {
  companion object {
    fun Documentation?.shortenedIfPresent(comma: String = ", ") = this.nullableToString(
      prefix = comma + "documentation='",
      suffix = "'",
      maxLength = 40,
      append = "..."
    )
  }

  override fun toString() = value
}
