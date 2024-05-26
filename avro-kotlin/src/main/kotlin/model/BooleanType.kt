package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isBooleanType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.ObjectProperties

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

  override fun toString() = toString("BooleanType") {
    addIfNotEmpty("properties", properties)
  }
}
