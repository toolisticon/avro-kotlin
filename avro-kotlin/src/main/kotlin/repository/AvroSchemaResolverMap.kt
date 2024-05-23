package io.toolisticon.avro.kotlin.repository

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import org.apache.avro.Schema

data class AvroSchemaResolverMap(
  private val store: Map<AvroFingerprint, AvroSchema> = emptyMap()
) : AvroSchemaResolver {
  companion object {
    val EMPTY = AvroSchemaResolverMap()

  }

  constructor(schema: Schema) : this((EMPTY + AvroSchema(schema)).store)
  constructor(schema: AvroSchema) : this((EMPTY + schema).store)

  override fun get(fingerprint: AvroFingerprint): AvroSchema = store[fingerprint] ?: throw missingSchemaException(
    fingerprint
  )

  operator fun plus(schema: AvroSchema): AvroSchemaResolverMap = copy(
    store = buildMap {
      putAll(store)
      put(schema.fingerprint, schema)
    }
  )

  operator fun plus(other: AvroSchemaResolverMap): AvroSchemaResolverMap = copy(store = buildMap {
    putAll(store)
    putAll(other.store)
  })
}
