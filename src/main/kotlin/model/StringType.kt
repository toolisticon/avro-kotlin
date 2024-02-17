package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.AvroKotlin.logicalTypeName
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.LogicalType

/**
 * Unicode character sequence
 */
@JvmInline
value class StringType(override val schema: AvroSchema) :
  AvroPrimitiveType,
  WithLogicalType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isStringType) { "Not a STRING type." }
  }

  override val type: SchemaType get() = SchemaType.STRING
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = logicalTypeName(logicalType)

  override fun toString() = toString("StringType") {
    addIfNotNull("documentation", schema.documentation)
    addIfNotNull("logicalType", logicalTypeName, "'")
    addIfNotEmpty("properties", properties)
  }
}
