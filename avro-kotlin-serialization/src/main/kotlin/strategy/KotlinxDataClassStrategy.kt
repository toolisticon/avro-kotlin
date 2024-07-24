package io.toolisticon.kotlin.avro.serialization.strategy

import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.serialization.isKotlinxDataClass
import org.apache.avro.generic.GenericRecord
import kotlin.reflect.KClass

class KotlinxDataClassStrategy(
  private val avroKotlinSerialization: AvroKotlinSerialization
) : GenericRecordSerializationStrategy {

  override fun test(serializedType: KClass<*>): Boolean = serializedType.isKotlinxDataClass()

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> deserialize(serializedType: KClass<*>, data: GenericRecord): T {
    return avroKotlinSerialization.genericRecordDecoder(serializedType).decode(data) as T
  }

  override fun <T : Any> serialize(data: T): GenericRecord {
    return avroKotlinSerialization.genericRecordEncoder<T>().encode(data)
  }
}
