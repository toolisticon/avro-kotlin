package io.toolisticon.kotlin.avro.serialization.cache

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import io.github.oshai.kotlinlogging.KotlinLogging
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolverMutableMap
import org.apache.avro.util.WeakIdentityHashMap
import kotlin.reflect.KClass

class AvroSchemaCache(
  private val avro4k: Avro,
  private val kSerializerCache: KSerializerCache,
  private val schemaResolver: AvroSchemaResolverMutableMap,
) : AvroCache.SchemaByClassCache {
  private val logger = KotlinLogging.logger {}

  private val store: WeakIdentityHashMap<KClass<*>, AvroSchema> = WeakIdentityHashMap()

  override operator fun get(klass: KClass<*>): AvroSchema = store.getOrPut(klass) {
    logger.trace { "add schema for $klass." }
    val serializer = kSerializerCache[klass]

    AvroSchema(avro4k.schema(serializer)).also { schemaResolver + it }
  }

  fun keys(): Set<KClass<*>> = store.keys.sortedBy { it.simpleName }.toSet()
  override fun toString() = "AvroSchemaCache(keys=${keys()})"
}
