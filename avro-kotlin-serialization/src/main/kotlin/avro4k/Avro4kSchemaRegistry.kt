package io.toolisticon.kotlin.avro.serialization.avro4k

import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import org.apache.avro.Schema
import org.apache.avro.message.MissingSchemaException

/**
 * Avro4k provides [com.github.avrokotlin.avro4k.AvroSingleObject], which does require a `(Long) -> Schema?` function
 * to resolve [org.apache.avro.Schema] from a store.
 *
 * This registry implements this requirement using our own [AvroSchemaResolver].
 */
@JvmInline
value class Avro4kSchemaRegistry(private val schemaResolver: AvroSchemaResolver) : (Long) -> Schema?{

  override fun invoke(fingerprint: Long): Schema? = try {
      schemaResolver.findByFingerprint(fingerprint)
  } catch (e: MissingSchemaException) {
    null
  }
}

/**
 * Extension fn to fluently crate a [Avro4kSchemaRegistry] from a [AvroSchemaResolver].
 */
fun AvroSchemaResolver.avro4k(): Avro4kSchemaRegistry = Avro4kSchemaRegistry(this)
