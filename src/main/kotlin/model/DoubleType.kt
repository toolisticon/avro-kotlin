package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.WithObjectProperties
import org.apache.avro.Schema

/**
 * Double precision (64-bit) IEEE 754 floating-point number
 */
@JvmInline
value class DoubleType(override val schema: AvroSchema) : AvroPrimitiveType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {
  init {
    require(schema.isDoubleType) { "Not a DOUBLE type." }
  }

  override val type: Schema.Type get() = Schema.Type.DOUBLE

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  override fun toString() = schema.name.toString()
}
