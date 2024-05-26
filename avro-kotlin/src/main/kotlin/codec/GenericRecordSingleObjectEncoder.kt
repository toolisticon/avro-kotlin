package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.value.ByteArrayValue
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
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
    return SingleObjectEncodedBytes.of(bytes = bytes)
  }
}
