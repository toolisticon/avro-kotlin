package io.toolisticon.kotlin.avro.serialization.avro4k

import com.github.avrokotlin.avro4k.AvroSingleObject
import com.github.avrokotlin.avro4k.encodeToByteArray
import io.toolisticon.kotlin.avro.codec.AvroCodec
import io.toolisticon.kotlin.avro.serialization.cache.KSerializerCache
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty0

@OptIn(ExperimentalSerializationApi::class)
class Avro4kSingleObjectCodec(
  private val avro4kSingleObject : AvroSingleObject,
  private val serializerCache: KSerializerCache
) {

  /**
   * @return a [AvroCodec.SingleObjectEncoder] based on [AvroSingleObject].
   */
  fun <T : Any> singleObjectEncoder(): AvroCodec.SingleObjectEncoder<T> = AvroCodec.SingleObjectEncoder<T> { data ->
//    val kclass: KClass<out T> = data::class
//    @Suppress("UNCHECKED_CAST")
//    val serializer: KSerializer<out T> = serializerCache.get(data::class)
//    val writerSchema = schema(data::class)
//
//    val bytes = avro4kSingleObject.encodeToByteArray(writerSchema.get(), serializer, data)
//
//    SingleObjectEncodedBytes.of(bytes)
    TODO()
  }

}
