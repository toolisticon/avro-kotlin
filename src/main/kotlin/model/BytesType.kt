package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.csv
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.nullableToString
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.LogicalType
import org.apache.avro.Schema

/**
 * Sequence of 8-bit unsigned bytes.
 */
@JvmInline
value class BytesType(override val schema: AvroSchema) : AvroPrimitiveType, WithLogicalType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isBytesType) { "Not a BYTES type." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val type: Schema.Type get() = Schema.Type.BYTES
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = AvroKotlin.logicalTypeName(logicalType)

  override fun toString() = "${this::class.simpleName}(" +
    csv(
      logicalType?.name.nullableToString("logicalType='", suffix = "'"),
      if (properties.isNotEmpty()) "properties=$properties" else null
    ) + ")"
}