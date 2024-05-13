package io.toolisticon.avro.kotlin.value

import _ktx.StringKtx.nullableToString
import _ktx.StringKtx.trimToNull
import org.apache.avro.Schema


/**
 * The documentation of a [Schema.Field] as defined in the `doc` element.
 */
@JvmInline
value class Documentation(override val value: String) : ValueType<String> {
  companion object {


    /**
     * Gets the nullable [Documentation] of the given string.
     */
    operator fun invoke(value: String?): Documentation? = value?.trimToNull()?.let { Documentation(it) }

    /**
     * Gets the nullabla [Documentation] of given [Schema].
     *
     * @see #documentation(String)
     */
    operator fun invoke(schema: Schema): Documentation? = invoke(schema.doc)

    operator fun invoke(field: Schema.Field): Documentation? = invoke(field.doc())

    fun Documentation?.shortenedIfPresent(comma: String = ", ") = this.nullableToString(
      prefix = comma + "documentation='",
      suffix = "'",
      maxLength = 40,
      append = "..."
    )
  }

  override fun toString() = value
}
