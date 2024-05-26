package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isMapType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.WithObjectProperties

/**
 * Represents an avro map type. Keys are `string`s by definition, values are of
 * schema type.
 *
 * Wraps [AvroSchema] for property access.
 */
class MapType(override val schema: AvroSchema) :
  AvroContainerType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isMapType) { "Not a map-type: $schema" }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  val valueType: AvroType get() = AvroType.avroType(schema.mapType!!)

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(valueType.schema) }

  override fun toString() = StringKtx.toString("MapType") {
    add("type", valueType.name)
    addIfNotNull("documentation", schema.documentation)
    addIfNotEmpty("properties", properties)
  }

  override fun equals(other: Any?): Boolean = equalsFn(other)
  override fun hashCode(): Int = hashCodeFn()
}
