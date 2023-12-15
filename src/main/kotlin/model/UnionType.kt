package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.WithObjectProperties

@JvmInline
value class UnionType(override val schema: AvroSchema) : AvroContainerType, SchemaSupplier by schema, WithObjectProperties by schema {

  init {
    require(schema.isUnionType) { "A unionType must contain ate least on type." }
  }

  override val name: Name get() = schema.name
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  val isNullable: Boolean get() = schema.isNullable
  val types: List<AvroSchema> get() = schema.unionTypes

  override fun toString() = "UnionType(types=${types.map { it.name }})"
}
