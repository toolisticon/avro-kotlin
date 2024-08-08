package io.toolisticon.kotlin.avro.serialization.cache

import io.toolisticon.kotlin.avro.serialization.isSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import mu.KLogging
import java.util.concurrent.ConcurrentHashMap
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
class KSerializerCache private constructor(
  private val serializersModule: SerializersModule,
  private val store: ConcurrentHashMap<KClass<*>, KSerializer<*>>
) : Set<KClass<*>> by store.keys {
  companion object : KLogging()

  constructor(serializersModule: SerializersModule) : this(serializersModule, ConcurrentHashMap())

  @Suppress("UNCHECKED_CAST")
  operator fun <T : Any> get(klass: KClass<out T>): KSerializer<out T> = store.computeIfAbsent(klass) { key ->
    require(key.isSerializable()) {"$klass is not serializable with kotlinx-serialization."}

    // TODO: if we use SpecificRecords, we could derive the schema from the class directly
    logger.trace { "add kserializer for $key." }

    // TODO: createType takes a lot of optional args. We probably won't need them but at least we should check them.
    val type: KType = key.createType()

    serializersModule.serializer(type)
  } as KSerializer<T>

  override fun toString() = "KSerializerCache(keys=${store.keys.sortedBy { it.simpleName }})"
}
