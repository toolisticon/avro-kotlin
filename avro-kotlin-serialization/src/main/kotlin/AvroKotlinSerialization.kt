package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializationModuleFactoryServiceLoader
import kotlinx.serialization.KSerializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class AvroKotlinSerialization(
  private val avro4k: Avro
) {

  private val kserializerCache = ConcurrentHashMap<KClass<*>, KSerializer<*>>()
  private val schemaCache = ConcurrentHashMap<KClass<*>, AvroSchema>()

  constructor() : this(
    Avro(AvroSerializationModuleFactoryServiceLoader())
  )

  fun serializer(type: KClass<*>) = kserializerCache.computeIfAbsent(type) { key ->
    key.kserializer()
  }

  fun schema(type: KClass<*>): AvroSchema = schemaCache.computeIfAbsent(type) { key ->
    AvroSchema(avro4k.schema(serializer(key)))
  }
}
