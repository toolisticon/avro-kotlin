package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isDoubleType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.WithObjectProperties

/**
 * Double precision (64-bit) IEEE 754 floating-point number
 */
@JvmInline
value class DoubleType(override val schema: AvroSchema) :
  AvroPrimitiveType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isDoubleType) { "Not a DOUBLE type." }
  }

  override val type: SchemaType get() = SchemaType.DOUBLE

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  override fun toString() = toString("DoubleType") {
    addIfNotEmpty("properties", properties)
  }
}
