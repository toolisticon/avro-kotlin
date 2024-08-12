package io.toolisticon.kotlin.avro.serialization.strategy

import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.serialization.isKotlinxEnumClass
import org.apache.avro.generic.GenericRecord
import kotlin.reflect.KClass

class KotlinxEnumClassStrategy(
  private val avroKotlinSerialization: AvroKotlinSerialization
) : GenericRecordSerializationStrategy {

  override fun test(serializedType: KClass<*>): Boolean = serializedType.isKotlinxEnumClass()

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> deserialize(serializedType: KClass<*>, data: GenericRecord): T {
    return avroKotlinSerialization.avro4kGenericRecordCodec.decoder(serializedType).decode(data) as T
  }

  override fun <T : Any> serialize(data: T): GenericRecord {
    return avroKotlinSerialization.avro4kGenericRecordCodec.encoder<T>().encode(data)
  }
}
