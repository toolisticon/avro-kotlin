package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.value.ByteArrayValue
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
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
    return SingleObjectEncodedBytes.of(bytes = bytes)
  }
}
