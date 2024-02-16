package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.WithObjectProperties


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

  override fun toString() = "UnionType(types=${types.map { it.name }})"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is UnionType) return false

    if (schema != other.schema) return false

    return true
  }

  override fun hashCode(): Int {
    return schema.hashCode()
  }
}
