package io.toolisticon.avro.kotlin

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.declaration.ProtocolDeclaration
import io.toolisticon.avro.kotlin.declaration.SchemaDeclaration
import io.toolisticon.avro.kotlin.logical.AvroLogicalType
import io.toolisticon.avro.kotlin.model.AvroType
import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.repository.AvroSchemaResolver
import io.toolisticon.avro.kotlin.value.*
import io.toolisticon.avro.kotlin.value.CanonicalName.Companion.toCanonicalName
import org.apache.avro.LogicalTypes
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.message.BinaryMessageEncoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecord
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.util.*
import kotlin.io.path.writeText
import kotlin.reflect.KClass

/**
 * Central declaration of constants and utils.
 */
object AvroKotlin {

  /**
   * Encapsulates [Schema#Parser#parse] for the relevant input types.
   */
  @Suppress("ClassName")
  object parseSchema {
    operator fun invoke(json: JsonString, isRoot: Boolean = false): AvroSchema = AvroSchema.of(json, isRoot)
    operator fun invoke(file: File): AvroSchema = AvroSchema.of(file)
    operator fun invoke(path: Path): AvroSchema = AvroSchema.of(path)
    operator fun invoke(resource: URL): AvroSchema = AvroSchema.of(resource)
    operator fun invoke(resource: String, classLoader: ClassLoader = DEFAULT_CLASS_LOADER): AvroSchema = invoke(resourceUrl(resource, null, classLoader))

    operator fun invoke(recordClass: Class<*>): AvroSchema {
      require(SpecificRecord::class.java.isAssignableFrom(recordClass)) { "recordClass needs to be SpecificRecord: ${recordClass.simpleName}" }
      val schema: Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)
      return AvroSchema(schema)
    }

    operator fun invoke(recordClass: KClass<*>): AvroSchema = invoke(recordClass.java)
  }

  @Suppress("ClassName")
  object parseProtocol {
    private fun parse(inputStream: InputStream) = AvroProtocol(
      protocol = Protocol.parse(inputStream)
    )

    operator fun invoke(json: JsonString): AvroProtocol = parse(json.inputStream())
    operator fun invoke(file: File): AvroProtocol = parse(file.inputStream())
    operator fun invoke(resource: URL): AvroProtocol = parse(resource.openStream())
    operator fun invoke(resource: String): AvroProtocol = invoke(resourceUrl(resource))
  }

  /**
   * Marker bytes according to Avro schema specification v1.
   */
  val AVRO_V1_HEADER = SingleObjectEncodedBytes.AVRO_V1_HEADER

  fun avroType(schema: AvroSchema): AvroType = AvroType.avroType(schema)

  @JvmStatic
  val genericData: GenericData get() = GenericData()

  @JvmStatic
  val specificData: SpecificData get() = SpecificData()


  fun canonicalName(namespace: String, name: String): CanonicalName = (namespace to name).toCanonicalName()

  fun createGenericRecord(schema: AvroSchema, receiver: GenericRecord.() -> Unit): GenericRecord = GenericData.Record(schema.get()).apply {
    receiver.invoke(this)
  }

  object Separator {

    /**
     * Default separator used in canonical name.
     */
    const val NAME = "."

    /**
     * File separator as defined by [File.separator].
     */
    val FILE = File.separator

    /**
     * Replace path file-separator dash with namespace dots.
     */
    fun dashToDot(string: String): String = string.replace(FILE, NAME)

    /**
     * Replace namespace dots with file-separator dash.
     */
    fun dotToDash(string: String): String = string.replace(NAME, FILE)

  }

  /**
   * Set of reserved keywords.
   */
  object Reserved {

    private val FIELD_RESERVED = Collections
      .unmodifiableSet(HashSet(mutableListOf("name", "type", "doc", "default", "aliases")))

    val SCHEMA: Set<String> = setOf("doc", "fields", "items", "name", "namespace", "size", "symbols", "values", "type", "aliases")
  }

  /**
   * Default charset to use is [Charsets#UTF_8]
   */
  val UTF_8 = Charsets.UTF_8


  /**
   * Default [ClassLoader] for resource loading.
   */
  val DEFAULT_CLASS_LOADER: ClassLoader = AvroKotlin::class.java.classLoader

  @Suppress("ClassName")
  object write {
    operator fun invoke(json: JsonString, path: Path) = path.writeText(json.value)
    operator fun invoke(json: JsonString, file: File) = file.writeText(json.value)

    operator fun invoke(schema: AvroSchema, directory: Directory) = directory.write(
      fqn = schema.canonicalName,
      type = AvroSpecification.SCHEMA,
      content = schema.json
    )

    operator fun invoke(schema: SchemaDeclaration, directory: Directory) = directory.write(
      fqn = schema.canonicalName,
      type = AvroSpecification.SCHEMA,
      content = schema.source.json
    )

    operator fun invoke(protocol: AvroProtocol, directory: Directory) = directory.write(
      fqn = protocol.canonicalName,
      type = AvroSpecification.PROTOCOL,
      content = protocol.json
    )

    operator fun invoke(protocol: ProtocolDeclaration, directory: Directory) = directory.write(
      fqn = protocol.canonicalName,
      type = AvroSpecification.PROTOCOL,
      content = protocol.source.json
    )
  }

  @JvmStatic
  fun avroSchemaResolver(schema: Schema) = io.toolisticon.avro.kotlin.repository.avroSchemaResolver(
    firstSchema = AvroSchema(schema)
  )

  val avroLogicalTypes by lazy {
    LogicalTypes.getCustomRegisteredTypes().values.filterIsInstance<AvroLogicalType<*>>()
      .toList()
  }

  fun genericRecordToJson(record: GenericRecord, genericData: GenericData): JsonString {

    val jsonBytes = ByteArrayOutputStream().use {
      val dw = GenericDatumWriter<Any>(record.schema, genericData)
      val encoder = EncoderFactory.get().jsonEncoder(record.schema, it, true)
      dw.write(record, encoder)
      encoder.flush()
      it.toString(UTF_8)
    }

    return JsonString.of(jsonBytes)
  }

  fun genericRecordFromJson(
    json: JsonString,
    readerSchema: AvroSchema,
    writerSchema: AvroSchema = readerSchema,
    genericData: GenericData = AvroKotlin.genericData
  ): GenericRecord {
    val reader = GenericDatumReader<Any>(writerSchema.get(), readerSchema.get(), genericData)

    return reader.read(null, DecoderFactory.get().jsonDecoder(readerSchema.get(), json.inputStream())) as GenericRecord
  }

  fun genericRecordToSingleObjectEncoded(
    record: GenericRecord,
    genericData: GenericData = AvroKotlin.genericData
  ): SingleObjectEncodedBytes {
    val bytes = ByteArrayValue(ByteArrayOutputStream().use { baos ->
      BinaryMessageEncoder<GenericRecord>(genericData, record.schema).encode(record, baos)
      baos.toByteArray()
    })
    return SingleObjectEncodedBytes.of(bytes)
  }

  fun genericRecordFromSingleObjectEncoded(
    singleObjectEncodedBytes: SingleObjectEncodedBytes,
    readerSchema: AvroSchema,
    schemaStore: AvroSchemaResolver,
    genericData: GenericData = AvroKotlin.genericData
  ): GenericRecord {
    return BinaryMessageDecoder<GenericRecord>(genericData, readerSchema.get(), schemaStore).decode(singleObjectEncodedBytes.value)
  }

  fun genericRecordFromPayloadBytes(
    payloadBytes: BinaryEncodedBytes,
    readerSchema: AvroSchema,
    writerSchema: AvroSchema = readerSchema,
    genericData: GenericData = AvroKotlin.genericData
  ): GenericRecord {
    val decoder = DecoderFactory.get().binaryDecoder(payloadBytes.value, null)

    val reader = GenericDatumReader<Any>(writerSchema.get(), readerSchema.get(), genericData)

    return reader.read(null, decoder) as GenericRecord
  }

  fun <T : Any> Result<List<T>?>.orEmpty(): List<T> = getOrNull() ?: emptyList()


}
