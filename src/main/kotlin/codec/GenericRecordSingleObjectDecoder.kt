package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin.defaultLogicalTypeConversions
import io.toolisticon.avro.kotlin.AvroSchemaResolver
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.generic.GenericData
import org.apache.avro.message.BinaryMessageDecoder

class GenericRecordSingleObjectDecoder private constructor(
  private val writerSchema: AvroSchemaResolver,
  private val readerSchema: AvroSchemaResolver,
  private val genericData: GenericData,
) : AvroCodec.SingleObjectDecoder<GenericData.Record> {

  /**
   * Create a new [AvroCodec.Decoder] that only uses the
   * reader schema as single schema source.
   *
   * [decode] fails when writer schema is not equal to reader schema.
   */
  constructor(
    readerSchema: AvroSchema,
    genericData: GenericData = defaultLogicalTypeConversions.genericData,
  ) : this(
    readerSchema = { readerSchema },
    writerSchema = { readerSchema },
    genericData = genericData
  )

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun decode(singleObjectEncodedBytes: SingleObjectEncodedBytes): GenericData.Record {

    return BinaryMessageDecoder<GenericData.Record>(
      genericData,
      readerSchema().get()
    ).decode(singleObjectEncodedBytes.value)
  }
}
