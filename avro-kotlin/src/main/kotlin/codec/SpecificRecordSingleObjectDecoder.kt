package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.repository.AvroSchemaResolver
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Decoder that extracts the writer schema from the given single object bytes and resolves a
 * matching reader class.
 *
 * Attention: The returned instance is compliant to the reader schema, since the concrete schema
 * used to decode might differ from the one used for encoding.
 */
class SpecificRecordSingleObjectDecoder(
  private val writerSchemaResolver: AvroSchemaResolver
) : AvroCodec.SingleObjectDecoder<SpecificRecordBase> {

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun decode(singleObjectEncodedBytes: SingleObjectEncodedBytes): SpecificRecordBase {
    val writerFingerprint = singleObjectEncodedBytes.fingerprint
    val writerSchema = writerSchemaResolver[writerFingerprint]
    val writerSpecificData = SpecificData.getForSchema(writerSchema.get())

    @Suppress("UNCHECKED_CAST")
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
}
