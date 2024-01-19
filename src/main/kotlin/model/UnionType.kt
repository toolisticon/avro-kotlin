package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.WithObjectProperties

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
