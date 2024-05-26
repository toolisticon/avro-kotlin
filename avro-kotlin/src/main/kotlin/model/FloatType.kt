package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isFloatType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*

/**
 * Single precision (32-bit) IEEE 754 floating-point number.
 */
@JvmInline
value class FloatType(override val schema: AvroSchema) :
  AvroPrimitiveType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isFloatType) { "Not a FLOAT type." }
  }

  val documentation: Documentation? get() = schema.documentation
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val type: SchemaType get() = SchemaType.FLOAT

  override fun toString() = toString("FloatType") {
    addIfNotNull("documentation", documentation)
    addIfNotEmpty("properties", properties)
  }
}
