package io.toolisticon.kotlin.avro.serialization.strategy

import io.toolisticon.kotlin.avro.codec.SpecificRecordCodec
import io.toolisticon.kotlin.avro.serialization.isGeneratedSpecificRecordBase
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecordBase
import kotlin.reflect.KClass

class SpecificRecordBaseStrategy : GenericRecordSerializationStrategy {
  private val converter = SpecificRecordCodec.specificRecordToGenericRecordConverter()

  override fun test(serializedType: KClass<*>): Boolean = serializedType.isGeneratedSpecificRecordBase()

  override fun <T : Any> deserialize(serializedType: KClass<*>, data: GenericRecord): T {
    @Suppress("UNCHECKED_CAST")
    return SpecificRecordCodec.genericRecordToSpecificRecordConverter(serializedType.java).convert(data) as T
  }

  override fun <T : Any> serialize(data: T): GenericRecord = converter.convert(data as SpecificRecordBase)
}
