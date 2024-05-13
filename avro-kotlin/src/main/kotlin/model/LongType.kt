package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.isLongType
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.LogicalType

/**
 * 64-bit signed integer.
 */
@JvmInline
value class LongType(override val schema: AvroSchema) : AvroPrimitiveType,
  WithLogicalType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isLongType) { "Not a LONG type." }
  }

  override val type: SchemaType get() = SchemaType.LONG

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = LogicalTypeName(logicalType = logicalType)

  override fun toString() = StringKtx.toString("LongType") {
    addIfNotNull("documentation", schema.documentation)
    addIfNotNull("logicalType", logicalTypeName, "'")
    addIfNotEmpty("properties", properties.filterIgnoredKeys())
  }
}
