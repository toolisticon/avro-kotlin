package io.toolisticon.avro.kotlin.value

import _ktx.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.AvroKotlin.Separator.NAME
import org.apache.avro.Protocol
import org.apache.avro.Schema
import kotlin.io.path.Path

@JvmInline
value class Name(override val value: String) : ValueType<String> {

  /**
   * Get [Name] from given [Protocol].
   */
  constructor(protocol: Protocol) : this(protocol.name)

  /**
   * Get [Name] for given [Schema.Field].
   */
  constructor(field: Schema.Field) : this(field.name())

  /**
   * Get [Name] from given [Schema].
   */
  constructor(schema: Schema) : this(schema.name)

  operator fun plus(namespace: Namespace) = CanonicalName(namespace to this)

  fun suffix(suffix: String?): Name = Name("$value${suffix.trimToNull()?.replaceFirstChar(Char::uppercase) ?: ""}")

  fun toPath(specification: AvroSpecification) = toPath(specification.value)
  fun toPath(fileExtension: String) = Path("$value$NAME$fileExtension")

  override fun toString() = value
}
