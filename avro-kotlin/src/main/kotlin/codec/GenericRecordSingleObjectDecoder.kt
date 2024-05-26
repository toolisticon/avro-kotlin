package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.message.BinaryMessageDecoder

class GenericRecordSingleObjectDecoder private constructor(
    private val writerSchema: AvroSchemaResolver,
    private val readerSchema: AvroSchemaResolver,
    private val genericData: GenericData,
) : AvroCodec.SingleObjectDecoder<GenericRecord> {

  /**
   * Create a new [AvroCodec.Decoder] that only uses the
   * reader schema as single schema source.
   *
   * [decode] fails when writer schema is not equal to reader schema.
   */
  constructor(
    readerSchema: AvroSchema,
    genericData: GenericData,
  ) : this(
    readerSchema = { readerSchema },
    writerSchema = { readerSchema },
    genericData = genericData
  )

  override fun decode(encoded: SingleObjectEncodedBytes): GenericRecord {

    return BinaryMessageDecoder<GenericRecord>(
      genericData,
      readerSchema().get()
    ).decode(encoded.value)
  }
}
