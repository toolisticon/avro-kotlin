package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin._ktx.KotlinKtx.head
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import org.apache.avro.Schema
import org.apache.avro.message.MissingSchemaException
import org.apache.avro.message.SchemaStore

internal fun missingSchemaException(fingerprint: AvroFingerprint) =
  MissingSchemaException("Cannot resolve schema for fingerprint: $fingerprint[${fingerprint.value}]")

fun avroSchemaResolver(schemas: List<AvroSchema>): AvroSchemaResolver {
  val (first, other) = schemas.head()
  return avroSchemaResolver(first, *(other.toTypedArray()))
}


fun avroSchemaResolver(firstSchema: AvroSchema, vararg otherSchemas: AvroSchema): AvroSchemaResolver = object : AvroSchemaResolver {
  private val store = buildMap<AvroFingerprint, AvroSchema> {
    put(firstSchema.fingerprint, firstSchema)

    if (otherSchemas.isNotEmpty()) {
      putAll(otherSchemas.associateBy { it.fingerprint })
    } else {
      // in case we have a single schema, also provide this for key=NULL, so invoke works
      put(AvroFingerprint.NULL, firstSchema)
    }
  }

  override fun get(fingerprint: AvroFingerprint): AvroSchema = store[fingerprint] ?: throw missingSchemaException(fingerprint)
}

operator fun AvroSchemaResolver.plus(other: AvroSchemaResolver): AvroSchemaResolver = AvroSchemaResolver { fingerprint ->
  try {
    // first try us
    this[fingerprint]
  } catch (e: MissingSchemaException) {
    // then try other - raise exception if still no hit
    other[fingerprint]
  }
}

/**
 * Supply an [AvroSchema] by [AvroFingerprint].
 */
fun interface AvroSchemaResolver : SchemaStore {

  @Throws(MissingSchemaException::class)
  operator fun get(fingerprint: AvroFingerprint): AvroSchema

  @Throws(MissingSchemaException::class)
  operator fun invoke(): AvroSchema = this[AvroFingerprint.NULL]

  @Throws(MissingSchemaException::class)
  override fun findByFingerprint(fingerprint: Long): Schema = this[AvroFingerprint(fingerprint)].get()

}

data class AvroSchemaResolverMap(
  private val store: Map<AvroFingerprint, AvroSchema>
) : AvroSchemaResolver {

  override fun get(fingerprint: AvroFingerprint): AvroSchema = store[fingerprint] ?: throw missingSchemaException(fingerprint)

  operator fun plus(schema: AvroSchema): AvroSchemaResolverMap = copy(
    store = buildMap {
      putAll(store)
      put(schema.fingerprint, schema)
    }
  )

  operator fun plus(other: AvroSchemaResolverMap) : AvroSchemaResolverMap = copy(store = buildMap {
    putAll(store)
    putAll(other.store)
  })
}
