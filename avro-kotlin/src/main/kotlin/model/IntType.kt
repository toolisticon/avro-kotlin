package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isIntType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*
import io.toolisticon.kotlin.avro.value.property.LogicalTypeNameProperty
import org.apache.avro.LogicalType

/**
 * 32-bit signed integer.
 */
@JvmInline
value class IntType(override val schema: AvroSchema) : AvroPrimitiveType,
  WithLogicalType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {
  init {
    require(schema.isIntType) { "Not an INT type." }
  }

  override val type: SchemaType get() = SchemaType.INT
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = LogicalTypeNameProperty.from(properties)?.value

  override fun toString() = toString("IntType") {
    addIfNotNull("documentation", schema.documentation)
    addIfNotNull("logicalType", logicalTypeName, "'")
    addIfNotEmpty("properties", properties.filterIgnoredKeys())
  }
}
