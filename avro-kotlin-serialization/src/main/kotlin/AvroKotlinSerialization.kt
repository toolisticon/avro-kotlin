package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.decodeFromGenericData
import com.github.avrokotlin.avro4k.encodeToGenericData
import com.github.avrokotlin.avro4k.schema
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.compatibleToReadFrom
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializationModuleFactoryServiceLoader
import io.toolisticon.kotlin.avro.serialization.spi.SerializerModuleKtx.reduce
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import mu.KLogging
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
class AvroKotlinSerialization(
  private val avro4k: Avro
) {
  companion object : KLogging() {

    fun configure(vararg serializersModules: SerializersModule): AvroKotlinSerialization {
      return AvroKotlinSerialization(
        Avro {
          serializersModule = serializersModules.toList().reduce()
        }
      )
    }
  }

  private val kserializerCache = ConcurrentHashMap<KClass<*>, KSerializer<*>>()
  private val schemaCache = ConcurrentHashMap<KClass<*>, AvroSchema>()

  constructor() : this(
    Avro { serializersModule = AvroSerializationModuleFactoryServiceLoader() }
  )

  fun serializer(type: KClass<*>) = kserializerCache.computeIfAbsent(type) { key ->
    logger.trace { "add kserializer for $type." }
    key.kserializer()
  }

  fun schema(type: Class<*>): AvroSchema = schema(type.kotlin)

  fun schema(type: KClass<*>): AvroSchema = schemaCache.computeIfAbsent(type) { key ->
    logger.trace { "add schema for $type." }
    AvroSchema(avro4k.schema(serializer(key).descriptor))
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> toRecord(data: T): GenericRecord {
    val kserializer = serializer(data::class) as KSerializer<T>
    val schema = avro4k.schema(kserializer)

    return avro4k.encodeToGenericData(schema, kserializer, data) as GenericRecord
  }

  inline fun <reified T : Any> fromRecord(record: GenericRecord): T = fromRecord(record, T::class)

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> fromRecord(record: GenericRecord, type: KClass<T>): T {
    val writerSchema = AvroSchema(record.schema)

    val kserializer = serializer(type) as KSerializer<T>
    val readerSchema = schema(type)

    // TODO nicer?
    require(readerSchema.compatibleToReadFrom(writerSchema).result.incompatibilities.isEmpty()) { "Reader/writer schema are incompatible" }

    return avro4k.decodeFromGenericData(writerSchema = writerSchema.get(), deserializer = kserializer, record) as T
  }

  fun <T : Any> encodeSingleObject(
    value: T, genericData: GenericData = AvroKotlin.genericData
  ): SingleObjectEncodedBytes {

    val record = toRecord(value)

    return GenericRecordCodec.encodeSingleObject(
      record = record, genericData = genericData
    )
  }

  fun <T : Any> decodeFromSingleObject(
    singleObjectEncodedBytes: SingleObjectEncodedBytes,
    readerType: KClass<T>,
    schemaResolver: AvroSchemaResolver,
    genericData: GenericData = AvroKotlin.genericData
  ): T {
    val readerSchema = schema(readerType)

    val record = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = singleObjectEncodedBytes, readerSchema, genericData = genericData
    )

    return fromRecord(record, readerType)
  }

  // simplified reified version, can only be used for jdk > 17
  inline fun <reified T : Any> decodeFromSingleObject(
    schemaResolver: AvroSchemaResolver, singleObjectEncodedBytes: SingleObjectEncodedBytes, genericData: GenericData = AvroKotlin.genericData
  ): T = decodeFromSingleObject(
    singleObjectEncodedBytes = singleObjectEncodedBytes,
    schemaResolver = schemaResolver,
    genericData = genericData,
    readerType = T::class
  )

}
