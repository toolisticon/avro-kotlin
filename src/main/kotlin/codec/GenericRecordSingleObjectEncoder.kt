package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.value.ByteArrayValue
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.generic.GenericData
import org.apache.avro.message.BinaryMessageEncoder

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GenericRecordSingleObjectEncoder(
  private val genericData: GenericData = AvroCodec.defaultGenericData
) : AvroCodec.SingleObjectEncoder<GenericData.Record> {

  override fun encode(record: GenericData.Record): SingleObjectEncodedBytes {
    val writerSchema = record.schema

    val bytes = ByteArrayValue {
      BinaryMessageEncoder<GenericData.Record>(
        genericData,
        writerSchema
      ).encode(record, this)
    }
    return SingleObjectEncodedBytes(bytes = bytes)
  }
}
