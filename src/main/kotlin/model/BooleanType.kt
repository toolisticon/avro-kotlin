package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.csv
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.ObjectProperties
import org.apache.avro.Schema

/**
 * A binary value.
 */
@JvmInline
value class BooleanType(override val schema: AvroSchema) : AvroPrimitiveType, SchemaSupplier by schema {

  init {
    require(schema.isBooleanType) { "Not a BOOLEAN type." }
  }

  override val type: Schema.Type get() = Schema.Type.BOOLEAN
  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties


  override fun toString() = "${this::class.simpleName}(" +
    csv(
      if (properties.isNotEmpty()) "properties=$properties" else null
    ) +
    ")"
}
