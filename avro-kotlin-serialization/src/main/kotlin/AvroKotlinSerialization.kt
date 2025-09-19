package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.AvroSingleObject
import com.github.avrokotlin.avro4k.ExperimentalAvro4kApi
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolverMutableMap
import io.toolisticon.kotlin.avro.serialization.avro4k.Avro4kGenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.avro4k.Avro4kSingleObjectCodec
import io.toolisticon.kotlin.avro.serialization.avro4k.avro4k
import io.toolisticon.kotlin.avro.serialization.cache.AvroSchemaCache
import io.toolisticon.kotlin.avro.serialization.cache.KClassCache
import io.toolisticon.kotlin.avro.serialization.cache.KSerializerCache
import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializationModuleFactoryServiceLoader
import io.toolisticon.kotlin.avro.serialization.spi.SerializerModuleKtx.reduce
import io.toolisticon.kotlin.avro.value.AvroSchemaCompatibilityMap
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import java.lang.Runtime.Version
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class, ExperimentalAvro4kApi::class)
class AvroKotlinSerialization(
  val avro4k: Avro,
  private val schemaResolver: AvroSchemaResolverMutableMap = AvroSchemaResolverMutableMap.EMPTY,

  @PublishedApi
  internal val genericData: GenericData = AvroKotlin.genericData
) : AvroSchemaResolver by schemaResolver {
  companion object {

    fun configure(vararg serializersModules: SerializersModule): AvroKotlinSerialization {
      return AvroKotlinSerialization(
        Avro {
          serializersModule = serializersModules.toList().reduce()
        }
      )
    }

    // somehow parsing the version 1.8.0 fails ... this should fix it.
    internal fun version(vnum: String): Version {
      return try {
        Version.parse(vnum)
      } catch (e: Exception) {
        version(vnum.substringBeforeLast('.'))
      }
    }
  }

  private val kserializerCache = KSerializerCache(avro4k.serializersModule)
  private val kclassCache = KClassCache()
  private val schemaCache = AvroSchemaCache(avro4k, kserializerCache, schemaResolver)
  internal val avro4kSingleObject = AvroSingleObject(schemaRegistry = schemaResolver.avro4k(), avro = avro4k)
  internal val compatibilityCache = AvroSchemaCompatibilityMap()

  val avro4kSingleObjectCodec = Avro4kSingleObjectCodec(
    avro4kSingleObject = avro4kSingleObject,
    schemaResolver = schemaResolver,
    serializerCache = kserializerCache,
    schemaCache = schemaCache,
    kclassCache = kclassCache
  )

  val avro4kGenericRecordCodec = Avro4kGenericRecordCodec(
    avro4k = avro4k,
    serializerCache = kserializerCache,
    schemaCache = schemaCache,
    kclassCache = kclassCache,
    compatibilityCache = compatibilityCache
  )

  init {
    /**
     * We _need_ `kotlinx.serialization >= 1.7`. spring boot provides an outdated version (1.6.3).
     * This is a pita to resolve. This check makes sure, any misconfigurations are found on app-start.
     */
    val serializationVersion = KSerializer::class.java.`package`?.implementationVersion ?: "1.7"
    check(version(serializationVersion) >= version("1.7")) { "avro4k uses features that required kotlinx.serialization version >= 1.7.0. Make sure to include the correct versions, especially when you use spring-boot." }
  }

  constructor() : this(
    Avro { serializersModule = AvroSerializationModuleFactoryServiceLoader() }
  )

  /**
   * @see [Avro4kGenericRecordCodec]
   */
  fun <T : Any> encodeToGenericRecord(data: T): GenericRecord = avro4kGenericRecordCodec.encoder<T>().encode(data)

  /**
   * inline/reified for easier type access.
   * @see [AvroKotlinSerialization.decodeFromGenericRecord]
   */
  inline fun <reified T : Any> decodeFromGenericRecord(record: GenericRecord): T = decodeFromGenericRecord(record, T::class)

  /**
   * @see [Avro4kGenericRecordCodec]
   */
  fun <T : Any> decodeFromGenericRecord(record: GenericRecord, type: KClass<T>): T {
    return avro4kGenericRecordCodec.decoder(type).decode(record)
  }

  /**
   * @see [Avro4kSingleObjectCodec]
   */
  fun <T : Any> encodeToSingleObjectEncoded(data: T): SingleObjectEncodedBytes = avro4kSingleObjectCodec.encoder<T>().encode(data)

  fun <T : Any> decodeFromSingleObjectEncoded(encoded: SingleObjectEncodedBytes) = avro4kSingleObjectCodec.decoder<T>().decode(encoded)

  fun <T : Any> decodeFromSingleObjectEncoded(encoded: SingleObjectEncodedBytes, klass: KClass<T>) = avro4kSingleObjectCodec.decoder<T>(klass).decode(encoded)

  /**
   * @return kotlinx-serializer for given class.
   */
  fun serializer(klass: KClass<*>) = kserializerCache[klass]

  // TODO We would like to use the reified function from avro4k but we need to be able to dynamically load class by fqn
  fun schema(type: KClass<*>): AvroSchema = schemaCache[type]

  fun registerSchema(schema: AvroSchema): AvroKotlinSerialization = apply { schemaResolver + schema }

  fun cachedSerializerClasses(): Set<KClass<*>> = kserializerCache.keys()
  fun cachedSchemaClasses() = schemaCache.keys()
}
