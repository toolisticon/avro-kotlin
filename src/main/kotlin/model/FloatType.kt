package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.isFloatType
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*

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
