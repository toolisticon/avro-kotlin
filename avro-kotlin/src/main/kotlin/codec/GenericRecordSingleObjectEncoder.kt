package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.value.ByteArrayValue
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.message.BinaryMessageEncoder

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GenericRecordSingleObjectEncoder(
  private val genericData: GenericData
) : AvroCodec.SingleObjectEncoder<GenericRecord> {

  override fun encode(record: GenericRecord): SingleObjectEncodedBytes {
    val writerSchema = record.schema

    val bytes = ByteArrayValue {
      BinaryMessageEncoder<GenericRecord>(
        genericData,
        writerSchema
      ).encode(record, this)
    }
    return SingleObjectEncodedBytes(bytes = bytes)
  }
}
