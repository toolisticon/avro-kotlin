package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.*
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.codec.AvroCodec
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolverMutableMap
import io.toolisticon.kotlin.avro.serialization.avro4k.avro4k
import io.toolisticon.kotlin.avro.serialization.cache.KSerializerCache
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializationModuleFactoryServiceLoader
import io.toolisticon.kotlin.avro.serialization.spi.SerializerModuleKtx.reduce
import io.toolisticon.kotlin.avro.value.AvroSchemaCompatibilityMap
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import mu.KLogging
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import java.lang.Runtime.Version
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

@OptIn(ExperimentalSerializationApi::class)
class AvroKotlinSerialization(
  val avro4k: Avro,
  private val schemaResolver: AvroSchemaResolverMutableMap = AvroSchemaResolverMutableMap.EMPTY,

  @PublishedApi
  internal val genericData: GenericData = AvroKotlin.genericData
) : AvroSchemaResolver by schemaResolver {
  companion object : KLogging() {

    fun configure(vararg serializersModules: SerializersModule): AvroKotlinSerialization {
      return AvroKotlinSerialization(
        Avro {
          serializersModule = serializersModules.toList().reduce()
        }
      )
    }
  }

  init {
    /**
     * We _need_ `kotlinx.serialization >= 1.7`. spring boot provides an outdated version (1.6.3).
     * This is a pita to resolve. This check makes sure, any misconfigurations are found on app-start.
     */
    check(Version.parse(KSerializer::class.java.`package`.implementationVersion) >= Version.parse("1.7")) { "avro4k uses features that required kotlinx.serialization version >= 1.7.0. Make sure to include the correct versions, especially when you use spring-boot." }
  }

  internal val avro4kSingleObject = AvroSingleObject(schemaRegistry = schemaResolver.avro4k(), avro = avro4k)

  internal val compatibilityCache = AvroSchemaCompatibilityMap()

  private val kserializerCache = KSerializerCache(avro4k.serializersModule)
  private val schemaCache = ConcurrentHashMap<KClass<*>, AvroSchema>()

  constructor() : this(
    Avro { serializersModule = AvroSerializationModuleFactoryServiceLoader() }
  )


  /**
   * @return a [AvroCodec.SingleObjectEncoder] based on [AvroSingleObject].
   */
  fun <T : Any> singleObjectEncoder(): AvroCodec.SingleObjectEncoder<T> = AvroCodec.SingleObjectEncoder { data ->
    @Suppress("UNCHECKED_CAST")
    val serializer = serializer(data::class) as KSerializer<T>
    val writerSchema = schema(data::class)

    val bytes = avro4kSingleObject.encodeToByteArray(writerSchema.get(), serializer, data)

    SingleObjectEncodedBytes.of(bytes)
  }

  /**
   * @param klass - optional. If we do know the klass already, we can pass it to avoid a second lookup.
   * @return a [AvroCodec.SingleObjectDecoder] based on [AvroSingleObject].
   */
  fun <T : Any> singleObjectDecoder(klass: KClass<T>? = null): AvroCodec.SingleObjectDecoder<T> = AvroCodec.SingleObjectDecoder { bytes ->
    val writerSchema = schemaResolver[bytes.fingerprint]
    val klass: KClass<T> = AvroKotlin.loadClassForSchema<T>(writerSchema)

    @Suppress("UNCHECKED_CAST")
    avro4kSingleObject.decodeFromByteArray(serializer(klass), bytes.value) as T
  }

  /**
   * @return a [AvroCodec.GenericRecordEncoder] based on [Avro].
   */
  fun <T : Any> genericRecordEncoder(): AvroCodec.GenericRecordEncoder<T> = AvroCodec.GenericRecordEncoder { data ->
    @Suppress("UNCHECKED_CAST")
    val serializer = serializer(data::class) as KSerializer<T>
    val writerSchema = schema(data::class)

    avro4k.encodeToGenericData(writerSchema.get(), serializer, data) as GenericRecord
  }

  /**
   * @param klass - optional. If we do know the klass already, we can pass it to avoid a second lookup.
   * @return a [AvroCodec.SingleObjectDecoder] based on [Avro].
   */
  fun <T : Any> genericRecordDecoder(klass: KClass<T>? = null) = AvroCodec.GenericRecordDecoder { record ->
    val writerSchema = AvroSchema(record.schema)
    val readerKlass: KClass<T> = klass ?: AvroKotlin.loadClassForSchema(writerSchema)

    @Suppress("UNCHECKED_CAST")
    val kserializer = serializer(readerKlass) as KSerializer<T>
    val readerSchema = schema(readerKlass)
    val compatibility = compatibilityCache.compatibleToReadFrom(writerSchema, readerSchema)

    require(compatibility.isCompatible) { "Reader/writer schema are incompatible." }

    avro4k.decodeFromGenericData(writerSchema = writerSchema.get(), deserializer = kserializer, record)
  }

  /**
   * @see [AvroKotlinSerialization.genericRecordEncoder]
   */
  fun <T : Any> toGenericRecord(data: T): GenericRecord {
    @Suppress("UNCHECKED_CAST")
    val kserializer = serializer(data::class) as KSerializer<T>
    val schema = avro4k.schema(kserializer)

    return avro4k.encodeToGenericData(schema, kserializer, data) as GenericRecord
  }

  /**
   * inline/reified for easier type access.
   * @see [AvroKotlinSerialization.fromGenericRecord]
   */
  inline fun <reified T : Any> fromGenericRecord(record: GenericRecord): T = fromGenericRecord(record, T::class)

  /**
   * @see [AvroKotlinSerialization.genericRecordDecoder]
   */
  fun <T : Any> fromGenericRecord(record: GenericRecord, type: KClass<T>): T {
    return genericRecordDecoder(type).decode(record)
  }

  /**
   * @see [AvroKotlinSerialization.singleObjectEncoder]
   */
  fun <T : Any> toSingleObjectEncoded(data: T): SingleObjectEncodedBytes = singleObjectEncoder<T>().encode(data)

  fun <T: Any> fromSingleObjectEncoded(encoded: SingleObjectEncodedBytes) = singleObjectDecoder<T>().decode(encoded)

  /**
   * @return kotlinx-serializer for given class.
   */
  fun serializer(klass: KClass<*>) = kserializerCache[klass]

  // TODO We would like to use the reified function from avro4k but we need to be able to dynamically load class by fqn
  fun schema(type: KClass<*>): AvroSchema = schemaCache.computeIfAbsent(type) { key ->
    logger.trace { "add schema for $type." }
    AvroSchema(avro4k.schema(serializer(key))).also(this::registerSchema)
  }

  fun <T : Any> encodeSingleObject(value: T): SingleObjectEncodedBytes {

    val record = toGenericRecord(value)

    return GenericRecordCodec.encodeSingleObject(
      record = record, genericData = genericData
    )
  }

  fun <T : Any> decodeFromSingleObject(
    singleObjectEncodedBytes: SingleObjectEncodedBytes,
    readerType: KClass<T>,
    genericData: GenericData = this@AvroKotlinSerialization.genericData
  ): T {
    val readerSchema = schema(readerType)

    val record = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = singleObjectEncodedBytes, readerSchema, genericData = genericData
    )

    return fromGenericRecord(record, readerType)
  }

  // simplified reified version, can only be used for jdk > 17
  inline fun <reified T : Any> decodeFromSingleObject(
    singleObjectEncodedBytes: SingleObjectEncodedBytes, genericData: GenericData = this@AvroKotlinSerialization.genericData
  ): T = decodeFromSingleObject(
    singleObjectEncodedBytes = singleObjectEncodedBytes,
    genericData = genericData,
    readerType = T::class
  )

  fun registerSchema(schema: AvroSchema): AvroKotlinSerialization = apply { schemaResolver + schema }

  fun cachedSerializerClasses() : Set<KClass<*>> = kserializerCache
  fun cachedSchemaClasses() = schemaCache.keys().toList().toSet()
}
