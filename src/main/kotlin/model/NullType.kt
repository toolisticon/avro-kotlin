package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.ObjectProperties
import org.apache.avro.Schema

/**
 * Null - no value.
 */
data object NullType : AvroPrimitiveType {

  override val schema = AvroBuilder.primitiveSchema(type = Schema.Type.NULL)
  override fun get(): Schema = schema.get()

  override val type: Schema.Type = schema.type
  override val name: Name = schema.name
  override val hashCode: AvroHashCode = schema.hashCode
  override val fingerprint: AvroFingerprint = schema.fingerprint
  override val properties: ObjectProperties = ObjectProperties.EMPTY

  init {
    require(type == Schema.Type.NULL) { "not NULL type." }
  }
}
