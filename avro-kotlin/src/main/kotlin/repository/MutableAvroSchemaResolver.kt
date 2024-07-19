package io.toolisticon.kotlin.avro.repository

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import org.apache.avro.Schema
import org.apache.avro.message.MissingSchemaException
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [AvroSchemaResolver] that can register new schema definitions
 * and keeps all known instances in an in-memory map.
 */
@JvmInline
value class MutableAvroSchemaResolver private constructor(
  private val store: MutableMap<AvroFingerprint, AvroSchema> = ConcurrentHashMap()
) : AvroSchemaResolver, Map<AvroFingerprint, AvroSchema> by store {
  companion object {
    val EMPTY = MutableAvroSchemaResolver()
  }

  constructor(schema: Schema) : this((EMPTY + AvroSchema(schema)).store)
  constructor(schema: AvroSchema) : this((EMPTY + schema).store)

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  @Throws(MissingSchemaException::class)
  override fun get(fingerprint: AvroFingerprint): AvroSchema = store[fingerprint] ?: throw missingSchemaException(
    fingerprint
  )

  operator fun plus(schema: AvroSchema): MutableAvroSchemaResolver = apply {
    store[schema.fingerprint] = schema
  }

  operator fun plus(other: MutableAvroSchemaResolver): MutableAvroSchemaResolver = apply {
    store.putAll(other)
  }
}
