package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.ObjectProperties

/**
 * A binary value.
 */
@JvmInline
value class BooleanType(override val schema: AvroSchema) : AvroPrimitiveType, SchemaSupplier by schema {

  init {
    require(schema.isBooleanType) { "Not a BOOLEAN type." }
  }

  override val type: SchemaType get() = SchemaType.BOOLEAN
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties

  override fun toString() = StringKtx.toString("BooleanType") {
    addIfNotEmpty("properties", properties)
  }
}
