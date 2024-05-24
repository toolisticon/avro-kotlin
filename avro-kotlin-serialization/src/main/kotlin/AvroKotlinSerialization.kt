package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.codec.GenericRecordCodec
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.compatibleToReadFrom
import io.toolisticon.avro.kotlin.repository.AvroSchemaResolver
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializationModuleFactoryServiceLoader
import kotlinx.serialization.KSerializer
import mu.KLogging
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class AvroKotlinSerialization(
  private val avro4k: Avro
) {
  companion object : KLogging()

  private val kserializerCache = ConcurrentHashMap<KClass<*>, KSerializer<*>>()
  private val schemaCache = ConcurrentHashMap<KClass<*>, AvroSchema>()

  constructor() : this(
    Avro(AvroSerializationModuleFactoryServiceLoader())
  )

  fun serializer(type: KClass<*>) = kserializerCache.computeIfAbsent(type) { key ->
    logger.trace { "add kserializer for $type." }
    key.kserializer()
  }

  fun schema(type: Class<*>): AvroSchema = schema(type.kotlin)

  fun schema(type: KClass<*>): AvroSchema = schemaCache.computeIfAbsent(type) { key ->
    logger.trace { "add schema for $type." }
    AvroSchema(avro4k.schema(serializer(key)))
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> toRecord(data: T): GenericRecord {
    val kserializer = serializer(data::class) as KSerializer<T>

    return avro4k.toRecord(kserializer, data)
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> fromRecord(record: GenericRecord, type: KClass<T>): T {
    val writerSchema = AvroSchema(record.schema)

    val kserializer = serializer(type) as KSerializer<T>
    val readerSchema = schema(type)

    // TODO nicer?
    require(readerSchema.compatibleToReadFrom(writerSchema).result.incompatibilities.isEmpty()) { "Reader/writer schema are incompatible" }

    return avro4k.fromRecord(kserializer, record) as T
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
