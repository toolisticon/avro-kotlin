package io.toolisticon.kotlin.avro.serialization.strategy

import org.apache.avro.generic.GenericRecord
import java.util.function.Predicate
import kotlin.reflect.KClass

interface GenericRecordSerializationStrategy : Predicate<KClass<*>> {

  fun <T : Any> deserialize(serializedType: KClass<*>, data: GenericRecord): T

  fun <T : Any> serialize(data: T): GenericRecord
}
