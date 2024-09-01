package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isNullType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isOptionalType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.WithObjectProperties

/**
 * OptionalType is a special case of [io.toolisticon.kotlin.avro.model.UnionType].
 *
 * It contains the schema of the base type and the [io.toolisticon.kotlin.avro.model.NullType]
 * to indicate that its values can be either of base type or null.
 */
class OptionalType(override val schema: AvroSchema) :
  AvroContainerType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isOptionalType) { "An optionalType must contain exactly one schema and the null-schema." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint

  val isNullable: Boolean = true
  val type: AvroSchema = schema.unionTypes.first { !it.isNullType }

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(type) }

  override fun toString() = toString("OptionalType") {
    add("type", type.name)
    addIfNotNull("documentation", schema.documentation)
    addIfNotEmpty("properties", properties)
  }

  override fun equals(other: Any?) = equalsFn(other)
  override fun hashCode() = hashCodeFn()
}
