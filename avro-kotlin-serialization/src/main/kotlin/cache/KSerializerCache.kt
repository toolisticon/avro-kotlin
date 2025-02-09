package io.toolisticon.kotlin.avro.serialization.cache

import io.github.oshai.kotlinlogging.KotlinLogging
import io.toolisticon.kotlin.avro.serialization.isSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.apache.avro.util.WeakIdentityHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Provides [KSerializer]s for given [KClass] using kotlin-reflect and avro4k.
 * Values are cached for fast access of recurrent requests.
 *
 * Cache is in-mem only and not limited/evicted, as we will have one class/serializer pair for
 * each avro-message type, and it seems reasonable to expect that there won't be more than a few dozens.
 *
 * TODO: avro4k internally keeps st similar, it would be nice if their cache would be accessible, then we could use it here (or omit this class completely).
 */
class KSerializerCache(
  private val serializersModule: SerializersModule
) : AvroCache.SerializerByClassCache {
  private val logger = KotlinLogging.logger {}

  private val store: WeakIdentityHashMap<KClass<*>, KSerializer<*>> = WeakIdentityHashMap()

  @Suppress("UNCHECKED_CAST")
  override operator fun <T : Any> get(klass: KClass<out T>): KSerializer<T> = store.getOrPut(klass) {
    require(klass.isSerializable()) { "$klass is not serializable with kotlinx-serialization." }

    // TODO: if we use SpecificRecords, we could derive the schema from the class directly
    logger.trace { "add kserializer for $klass." }

    // TODO: createType takes a lot of optional args. We probably won't need them but at least we should check them.
    val type: KType = klass.createType()

    serializersModule.serializer(type)
  } as KSerializer<T>

  fun keys(): Set<KClass<*>> = store.keys.sortedBy { it.simpleName }.toSet()

  override fun toString() = "KSerializerCache(keys=${keys()})"
}
