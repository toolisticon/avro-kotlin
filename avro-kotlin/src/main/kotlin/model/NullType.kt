package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.value.*
import org.apache.avro.Schema

/**
 * Null - no value.
 */
data object NullType : AvroPrimitiveType {
  override val type: SchemaType = SchemaType.NULL

  override val schema = AvroBuilder.primitiveSchema(type = type)
  override fun get(): Schema = schema.get()

  override val name: Name = schema.name
  override val hashCode: AvroHashCode = schema.hashCode
  override val fingerprint: AvroFingerprint = schema.fingerprint
  override val properties: ObjectProperties = ObjectProperties.EMPTY

  override val json: JsonString = schema.json

  init {
    require(type == SchemaType.NULL) { "not NULL type." }
  }
}
