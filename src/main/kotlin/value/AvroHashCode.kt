package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaField
import org.apache.avro.Protocol
import org.apache.avro.Protocol.Message
import org.apache.avro.Schema

/**
 * Hashcode of a given Schema, uniquely identifying a Schema.
 *
 * [AvroFingerprint] is not sufficient to identify a unique [org.apache.avro.Schema]
 * because properties of logicalTypes are swallowed.
 *
 * So to determine correctly if we already parsed a [org.apache.avro.Schema]
 * we need to keep a tuple of fingerprint and hashCode.
 */
@JvmInline
value class AvroHashCode(override val value: Int) : WithHexString, Comparable<AvroHashCode>, ValueType<Int> {
  companion object {
    val NULL = AvroHashCode(0)
  }

  constructor(schema: AvroSchema) : this(schema.hashCode.value)
  constructor(field: AvroSchemaField) : this(field.schema.hashCode.value)

  constructor(message: Message) : this(message.hashCode())
  constructor(schema: Schema) : this(schema.hashCode())
  constructor(field: Schema.Field) : this(field.hashCode())
  constructor(protocol: Protocol) : this(protocol.hashCode())

  override val hex: HexString get() = HexString(value)
  override fun compareTo(other: AvroHashCode): Int = value.compareTo(other.value)

  override fun toString() = hex.value
}
