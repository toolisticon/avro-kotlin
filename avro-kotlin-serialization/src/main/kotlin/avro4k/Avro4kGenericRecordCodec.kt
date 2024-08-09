package io.toolisticon.kotlin.avro.serialization.avro4k

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.decodeFromGenericData
import com.github.avrokotlin.avro4k.encodeToGenericData
import io.toolisticon.kotlin.avro.codec.AvroCodec
import io.toolisticon.kotlin.avro.codec.AvroCodec.GenericRecordDecoder
import io.toolisticon.kotlin.avro.codec.AvroCodec.GenericRecordEncoder
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.serialization.cache.AvroCache
import io.toolisticon.kotlin.avro.value.AvroSchemaCompatibilityMap
import kotlinx.serialization.ExperimentalSerializationApi
import org.apache.avro.generic.GenericRecord
import kotlin.reflect.KClass

/**
 * Provides [GenericRecordDecoder] and [GenericRecordEncoder] using avro4k.
 */
@OptIn(ExperimentalSerializationApi::class)
class Avro4kGenericRecordCodec(
  private val avro4k: Avro,
  private val serializerCache: AvroCache.SerializerByClassCache,
  private val schemaCache: AvroCache.SchemaByClassCache,
  private val kclassCache: AvroCache.ClassBySchemaCache,
  private val compatibilityCache: AvroSchemaCompatibilityMap
) {

  /**
   * @return a [AvroCodec.GenericRecordEncoder] based on [Avro.encodeToGenericData].
   */
  fun <T : Any> encoder(): GenericRecordEncoder<T> = GenericRecordEncoder<T> { data ->
    val klass: KClass<out T> = data::class
    val serializer = serializerCache[klass]
    val writerSchema = schemaCache[klass]

    avro4k.encodeToGenericData(writerSchema.get(), serializer, data) as GenericRecord
  }

  /**
   * Looks up class and redirects to [Avro4kGenericRecordCodec.decoder(KClass)].
   *
   * @return a [AvroCodec.GenericRecordDecoder] based on [Avro.decodeFromGenericData].
   */
  fun <T : Any> decoder() = GenericRecordDecoder<T> { record ->
    val writerSchema = AvroSchema(record.schema)
    val readerKlass: KClass<T> = kclassCache[writerSchema]

    decoder(readerKlass).decode(record)
  }

  /**
   * Reader class can be passed in to avoid additional lookup and avoid type erasure of <T>.
   * @return a [AvroCodec.GenericRecordDecoder] based on [Avro.decodeFromGenericData].
   */
  fun <T : Any> decoder(readerKlass: KClass<T>) = GenericRecordDecoder<T> { record ->
    val writerSchema = AvroSchema(record.schema)

    val kserializer = serializerCache[readerKlass]
    val readerSchema = schemaCache[readerKlass]
    val compatibility = compatibilityCache.compatibleToReadFrom(writerSchema, readerSchema)

    require(compatibility.isCompatible) { "Reader/writer schema are incompatible." }

    avro4k.decodeFromGenericData(writerSchema = writerSchema.get(), deserializer = kserializer, record)
  }
}
