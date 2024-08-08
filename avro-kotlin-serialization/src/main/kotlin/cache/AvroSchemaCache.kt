package io.toolisticon.kotlin.avro.serialization.cache

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolverMutableMap
import mu.KLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class AvroSchemaCache private constructor(
  private val avro4k: Avro,
  private val schemaResolver: AvroSchemaResolverMutableMap,
  private val store: ConcurrentHashMap<KClass<*>, AvroSchema>
) {
  companion object : KLogging()

  operator fun get(klass: KClass<*>): AvroSchema = store.computeIfAbsent(klass) { key ->
    logger.trace { "add schema for $key." }
    //AvroSchema(avro4k.schema(serializer(key))).also(this::registerSchema)
    TODO()
  }
}
