package io.toolisticon.kotlin.avro.serialization.avro4k

import com.github.avrokotlin.avro4k.AvroSingleObject
import com.github.avrokotlin.avro4k.ExperimentalAvro4kApi
import com.github.avrokotlin.avro4k.encodeToByteArray
import io.toolisticon.kotlin.avro.codec.AvroCodec
import io.toolisticon.kotlin.avro.codec.AvroCodec.SingleObjectDecoder
import io.toolisticon.kotlin.avro.codec.AvroCodec.SingleObjectEncoder
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.serialization.cache.AvroCache
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.reflect.KClass

/**
 * Provides [SingleObjectDecoder] and [SingleObjectEncoder] using avro4k.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalAvro4kApi::class)
class Avro4kSingleObjectCodec(
  private val avro4kSingleObject: AvroSingleObject,
  private val schemaResolver: AvroSchemaResolver,
  private val serializerCache: AvroCache.SerializerByClassCache,
  private val schemaCache: AvroCache.SchemaByClassCache,
  private val kclassCache: AvroCache.ClassBySchemaCache
) {

  /**
   * @return a [AvroCodec.SingleObjectEncoder] based on [AvroSingleObject].
   */
  fun <T : Any> encoder(): SingleObjectEncoder<T> = SingleObjectEncoder<T> { data ->
    val klass: KClass<out T> = data::class
    val serializer = serializerCache[klass]
    val writerSchema = schemaCache[klass]

    val bytes = avro4kSingleObject.encodeToByteArray(writerSchema.get(), serializer, data)
    SingleObjectEncodedBytes.of(bytes)
  }

  /**
   * @return a [AvroCodec.SingleObjectDecoder] based on [AvroSingleObject].
   */
  fun <T : Any> decoder(): SingleObjectDecoder<T> = SingleObjectDecoder<T> { encoded ->
    val writerSchema = schemaResolver[encoded.fingerprint]
    val klass = kclassCache.get<T>(writerSchema)

    decoder(klass).decode(encoded)
  }

  /**
   * Target klass can be passed in to avoid additional lookup and avoid type erasure.
   *
   * @return a [AvroCodec.SingleObjectDecoder] based on [AvroSingleObject].
   */
  fun <T : Any> decoder(klass: KClass<T>): SingleObjectDecoder<T> = SingleObjectDecoder<T> { encoded ->
    val serializer = serializerCache[klass]

    avro4kSingleObject.decodeFromByteArray(serializer, encoded.value)
  }
}
