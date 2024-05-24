package io.toolisticon.avro.kotlin.value

import _ktx.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.AvroKotlin.Separator.NAME
import org.apache.avro.Protocol
import org.apache.avro.Schema
import kotlin.io.path.Path

@JvmInline
value class Name(override val value: String) : ValueType<String> {
  companion object {
    val EMPTY = Name("")

    fun String.toName() = Name(this)

    /**
     * Get [Name] from given [Protocol].
     */
    fun of(protocol: Protocol) = Name(protocol.name)

    /**
     * Get [Name] for given [Schema.Field].
     */
    fun of(field: Schema.Field) = Name(field.name())

    /**
     * Get [Name] from given [Schema].
     */
    fun of(schema: Schema) = Name(schema.name)
  }

  operator fun plus(namespace: Namespace) = CanonicalName(namespace, this)

  fun suffix(suffix: String?): Name = Name("$value${suffix.trimToNull()?.replaceFirstChar(Char::uppercase) ?: ""}")

  fun toPath(specification: AvroSpecification) = toPath(specification.value)
  fun toPath(fileExtension: String) = Path("$value$NAME$fileExtension")

  override fun toString() = value
}
