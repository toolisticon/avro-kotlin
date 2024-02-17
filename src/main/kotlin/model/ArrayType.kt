package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.model.AvroType.Companion.equalsFn
import io.toolisticon.avro.kotlin.model.AvroType.Companion.hashCodeFn
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.ObjectProperties

/**
 * Represents an avro array type.
 *
 * Wraps [AvroSchema] for property access.
 */
class ArrayType(override val schema: AvroSchema) :
  AvroContainerType,
  SchemaSupplier by schema {

  init {
    require(schema.isArrayType) { "Not an ARRAY type, was=${schema.type}." }
    requireNotNull(schema.arrayType) { "ElementType is required for ArrayType." }
  }

  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties

  val elementType: AvroType = AvroType.avroType(schema.arrayType!!)

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(elementType.schema) }

  override fun toString() = toString("ArrayType") {
    add("type", elementType.name)
    addIfNotEmpty("properties", properties)
  }

  override fun equals(other: Any?): Boolean = equalsFn(other)
  override fun hashCode(): Int = hashCodeFn()
}
