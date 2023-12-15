package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.ObjectProperties

/**
 * Represents an avro array type.
 *
 * Wraps [AvroSchema] for property access.
 */
@JvmInline
value class ArrayType(override val schema: AvroSchema) : AvroContainerType, SchemaSupplier by schema {

  init {
    require(schema.isArrayType) { "Not an ARRAY type." }
    requireNotNull(schema.arrayType) { "ElementType is required for ArrayType." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties

  val elementType: AvroSchema get() = schema.arrayType!!
  val elementHashCode: AvroHashCode get() = elementType.hashCode

  override fun toString() = "${this::class.simpleName}(type=${elementType.name})"
}
