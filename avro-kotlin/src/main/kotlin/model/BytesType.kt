package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isBytesType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*
import org.apache.avro.LogicalType

/**
 * Sequence of 8-bit unsigned bytes.
 */
@JvmInline
value class BytesType(override val schema: AvroSchema) :
  AvroPrimitiveType,
  WithLogicalType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isBytesType) { "Not a BYTES type." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val type: SchemaType get() = SchemaType.BYTES
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = LogicalTypeName.ofNullable(logicalType = logicalType)

  override fun toString() = toString("BytesType") {
    addIfNotNull(property = "logicalType", value = logicalType?.name, wrap = "'")
    addIfNotEmpty("properties", properties.filterIgnoredKeys())
  }
}
