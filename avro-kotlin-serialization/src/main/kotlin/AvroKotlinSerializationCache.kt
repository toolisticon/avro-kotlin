package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import kotlinx.serialization.KSerializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Internal in-mem cache that keeps serializers, schema, derived types, ...
 */
internal class AvroKotlinSerializationCache(
  private val avro4k: Avro
) {

  private val serializersByType: ConcurrentHashMap<KClass<*>, KSerializer<*>> = ConcurrentHashMap()
  private val schemasByType :  ConcurrentHashMap<KClass<*>, AvroSchema> = ConcurrentHashMap()
}
