package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.*
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.codec.AvroCodec
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.compatibleToReadFrom
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.repository.MutableAvroSchemaResolver
import io.toolisticon.kotlin.avro.serialization.avro4k.avro4k
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializationModuleFactoryServiceLoader
import io.toolisticon.kotlin.avro.serialization.spi.SerializerModuleKtx.reduce
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import mu.KLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

@OptIn(ExperimentalSerializationApi::class)
class AvroKotlinSerialization(
  private val avro4k: Avro,
  private val schemaResolver: MutableAvroSchemaResolver = MutableAvroSchemaResolver.EMPTY,
  private val genericData: GenericData = AvroKotlin.genericData
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

  @PublishedApi
  internal val avro4kSingleObject = AvroSingleObject(schemaRegistry = schemaResolver.avro4k(), avro = avro4k)
  private val kserializerCache = ConcurrentHashMap<KClass<*>, KSerializer<*>>()
  private val schemaCache = ConcurrentHashMap<KClass<*>, AvroSchema>()

  constructor() : this(
    Avro { serializersModule = AvroSerializationModuleFactoryServiceLoader() }
  )

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> toGenericRecord(data: T): GenericRecord {
    val kserializer = serializer(data::class) as KSerializer<T>
    val schema = avro4k.schema(kserializer)

    return avro4k.encodeToGenericData(schema, kserializer, data) as GenericRecord
  }

  fun <T : Any> fromGenericRecord(record: GenericRecord): T {
    TODO("Not yet implemented")
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> toSingleObjectEncoded(data: T): SingleObjectEncodedBytes {
    val serializer = serializer(data::class) as KSerializer<T>
    val writerSchema = schema(data::class)

    val bytes = avro4kSingleObject.encodeToByteArray(writerSchema.get(), serializer, data)

    return SingleObjectEncodedBytes.of(bytes)
  }

  /**
   * @return kotlinx-serializer for given class.
   */
  fun serializer(klass: KClass<*>) = kserializerCache.computeIfAbsent(klass) { key ->
    // TODO: if we use SpecificRecords, we could derive the schema from the class directly
    logger.trace { "add kserializer for $key." }

    // TODO: createType takes a lot of optional args. We probably won't need them but at least we should check them.
    val type = key.createType()

    avro4k.serializersModule.serializer(type)
  }

  // TODO We would like to use the reified function from avro4k but we need to be able to dynamically load class by fqn
  fun schema(type: KClass<*>): AvroSchema = schemaCache.computeIfAbsent(type) { key ->
    logger.trace { "add schema for $type." }
    AvroSchema(avro4k.schema(serializer(key))).also(this::registerSchema)
  }

  @Deprecated("use toGenericRecord instead", ReplaceWith("toGenericRecord(data"))
  fun <T : Any> toRecord(data: T): GenericRecord = toGenericRecord(data)

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

  fun registerSchema(schema: AvroSchema): AvroKotlinSerialization = apply { schemaResolver + schema }
}
