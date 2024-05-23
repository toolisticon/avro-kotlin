@file:JvmName("RepositoryKt")
package io.toolisticon.avro.kotlin.repository

import io.toolisticon.avro.kotlin._ktx.KotlinKtx.head
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import org.apache.avro.message.MissingSchemaException

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

