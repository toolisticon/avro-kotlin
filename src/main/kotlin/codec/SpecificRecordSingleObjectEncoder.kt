package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.value.ByteArrayValue
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.message.BinaryMessageEncoder
import org.apache.avro.specific.SpecificRecordBase

class SpecificRecordSingleObjectEncoder : AvroCodec.SingleObjectEncoder<SpecificRecordBase> {

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun encode(record: SpecificRecordBase): SingleObjectEncodedBytes {
    val bytes = ByteArrayValue {
      BinaryMessageEncoder<SpecificRecordBase>(
        record.specificData,
        record.schema
      ).encode(record, this)
    }
    return SingleObjectEncodedBytes(bytes = bytes)
  }
}
