package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.WithObjectProperties

/**
 * Represents an avro map type. Keys are `string`s by definition, values are of
 * schema type.
 *
 * Wraps [AvroSchema] for property access.
 */
@JvmInline
value class MapType(override val schema: AvroSchema) : AvroContainerType, SchemaSupplier by schema, WithObjectProperties by schema {

  init {
    require(schema.isMapType) { "Not a map-type: $schema" }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  val valueType: AvroSchema get() = schema.mapType!!

  override fun toString() = "MapType(type=${valueType.name})"
}
