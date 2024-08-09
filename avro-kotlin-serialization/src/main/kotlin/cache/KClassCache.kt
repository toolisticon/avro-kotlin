package io.toolisticon.kotlin.avro.serialization.cache

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class KClassCache() {

  private val store = ConcurrentHashMap<AvroSchema, KClass<*>>()


  @Suppress("UNCHECKED_CAST")
  operator fun <T : Any> get(schema: AvroSchema): KClass<T> = store.computeIfAbsent(schema) { key ->
    AvroKotlin.loadClassForSchema<T>(key)
  } as KClass<T>
}
