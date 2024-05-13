package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.value.*
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
