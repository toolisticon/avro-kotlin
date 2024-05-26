package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isArrayType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.ObjectProperties

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
