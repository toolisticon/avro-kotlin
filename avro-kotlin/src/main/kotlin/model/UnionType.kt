package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isNullable
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isUnionType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.WithObjectProperties

/**
 * Unions, as mentioned above, are represented using JSON arrays. For example, ["null", "string"] declares a schema which may be either a null or string.
 *
 * (Note that when a default value is specified for a record field whose type is a union, the type of the default value must match the first element of the union. Thus, for unions containing “null”, the “null” is usually listed first, since the default value of such unions is typically null.)
 *
 * Unions may not contain more than one schema with the same type, except for the named types record, fixed and enum. For example, unions containing two array types or two map types are not permitted, but two types with different names are permitted. (Names permit efficient resolution when reading and writing unions.)
 *
 * Unions may not immediately contain other unions.
 */
class UnionType(override val schema: AvroSchema) :
  AvroContainerType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    require(schema.isUnionType) { "A unionType must contain ate least on type." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  val isNullable: Boolean get() = schema.isNullable
  val types: List<AvroSchema> = schema.unionTypes

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(types) }


  fun reduce(): AvroSchema {
    // we have a union to achieve nullability
    if (types.size == 2 && isNullable) {
      return (types.first { it.type != SchemaType.NULL })
    }

    return this.schema
  }

  override fun toString() = toString("UnionType") {
    add("types", types.map { it.name })
    addIfNotNull("documentation", schema.documentation)
    addIfNotEmpty("properties", properties)
  }

  override fun equals(other: Any?) = equalsFn(other)
  override fun hashCode() = hashCodeFn()
}
