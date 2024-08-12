package io.toolisticon.kotlin.avro.serialization.cache

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

object AvroCache {

  interface SchemaByClassCache {
    operator fun get(klass: KClass<*>): AvroSchema
  }

  interface ClassBySchemaCache {
    operator fun <T : Any> get(schema: AvroSchema): KClass<T>
  }

  interface SerializerByClassCache {
    operator fun <T : Any> get(klass: KClass<out T>): KSerializer<T>
  }
}
