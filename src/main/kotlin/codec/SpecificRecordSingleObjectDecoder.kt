package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin.defaultLogicalTypeConversions
import io.toolisticon.avro.kotlin.AvroSchemaResolver
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase

class SpecificRecordSingleObjectDecoder(
  val writerSchemaResolver: AvroSchemaResolver
) : AvroCodec.SingleObjectDecoder<SpecificRecordBase> {

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun decode(singleObjectEncodedBytes: SingleObjectEncodedBytes): SpecificRecordBase {
    val writerFingerprint = singleObjectEncodedBytes.fingerprint
    val writerSchema = writerSchemaResolver[writerFingerprint]
    val writerSpecificData = SpecificData.getForSchema(writerSchema.get())

    val readerClass = writerSpecificData.getClass(writerSchema.get()) as Class<SpecificRecordBase>
    val readerSpecificData = SpecificData.getForClass(readerClass)

    val readerSchema = SpecificRecordCodec.avroSchema(readerClass)

    return BinaryMessageDecoder<SpecificRecordBase>(
      readerSpecificData,
      readerSchema.get()
    ).apply {
      addSchema(writerSchema.get())
    }.decode(singleObjectEncodedBytes.value)
  }

//  /**
//   * Decoder that extracts the writer schema from the given single object bytes and resolves a
//   * matching reader class using [SchemaResolutionSupport].
//   *
//   * Attention: The returned instance is compliant to the reader schema, since the concrete schema
//   * used to decode might differ from the one used for encoding.
//   */
//  open class DefaultSingleObjectToSpecificRecordDecoder(
//    private val schemaStore: SchemaStore,
//    private val schemaResolutionSupport: SchemaResolutionSupport
//  ) : SingleObjectToSpecificRecordDecoder {
//
//    @JvmOverloads
//    constructor(
//      schemaResolver: AvroSchemaResolver,
//      decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
//      schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
//    ) : this(
//      schemaStore = DefaultSchemaStore(schemaResolver),
//      schemaResolutionSupport = SchemaResolutionSupport(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)
//    )
//
//    override fun <T : SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded): T {
//      // resolve reader schema
//      val resolvedReaderSchema = schemaResolutionSupport.resolveReaderSchema(bytes).schema
//      // construct decoder and decode
//      return BinaryMessageDecoder<T>(SpecificData(), resolvedReaderSchema, schemaStore).decode(bytes)
//    }
}
