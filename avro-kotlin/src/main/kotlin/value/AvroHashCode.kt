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

    fun of(schema: AvroSchema) = AvroHashCode(schema.hashCode.value)
    fun of(field: AvroSchemaField) = AvroHashCode(field.schema.hashCode.value)

    fun of(message: Message) = AvroHashCode(message.hashCode())
    fun of(schema: Schema) = AvroHashCode(schema.hashCode())
    fun of(field: Schema.Field) = AvroHashCode(field.hashCode())
    fun of(protocol: Protocol) = AvroHashCode(protocol.hashCode())
    fun of(hex: String) = of(HexString.parse(hex))
    fun of(hex: HexString) = AvroHashCode(value = hex.parseInt())
  }

  override val hex: HexString get() = HexString.of(value)
  override fun compareTo(other: AvroHashCode): Int = value.compareTo(other.value)

  override fun toString() = hex.value
}
