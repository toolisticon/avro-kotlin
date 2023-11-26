package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name

/**
 * Represents an avro array type.
 *
 * Wraps [AvroSchema] for property access.
 */
@JvmInline
value class ArrayType(override val schema: AvroSchema) : AvroContainerType, SchemaSupplier by schema {

  init {
    require(schema.isArrayType)
    requireNotNull(schema.arrayType) { "elementType is required for ArrayType." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  val elementType: AvroSchema get() = schema.arrayType!!
  val elementHashCode: AvroHashCode get() = elementType.hashCode

  override fun toString() = schema.name.toString().removeSuffix(")") +
    "elementType=${elementType.name}" +
    ")"

}

/**
 * Represents an avro map type. Keys are `string`s by definition, values are of
 * schema type.
 *
 * Wraps [AvroSchema] for property access.
 */
@JvmInline
value class MapType(override val schema: AvroSchema) : AvroContainerType, SchemaSupplier by schema {

  init {
    require(schema.isMapType) { "Not a map-type: $schema" }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  val valueType: AvroSchema get() = schema.mapType!!

  override fun toString() = schema.name.toString().removeSuffix(")") +
    "valueType=${valueType.name}" +
    ")"
}

@JvmInline
value class UnionType(override val schema: AvroSchema) : AvroContainerType, SchemaSupplier by schema {

  init {
    require(schema.isUnionType)
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  val isNullable: Boolean get() = schema.isNullable
  val types: List<AvroSchema> get() = schema.unionTypes

  override fun toString() = schema.name.toString().removeSuffix(")") +
    "types=${types.map { it.name }}" +
    ")"
}
