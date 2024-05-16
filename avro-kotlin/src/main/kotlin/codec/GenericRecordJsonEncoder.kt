package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.codec.AvroCodec.JsonEncoder
import io.toolisticon.avro.kotlin.codec.AvroCodec.encoderFactory
import io.toolisticon.avro.kotlin.value.ByteArrayValue
import io.toolisticon.avro.kotlin.value.JsonString
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GenericRecordJsonEncoder(
  private val genericData: GenericData
) : JsonEncoder<GenericRecord> {

  override fun encode(record: GenericRecord): JsonString {
    val writerSchema: Schema = record.schema


    val jsonBytes = ByteArrayValue {
      val writer = GenericDatumWriter<Any>(writerSchema, genericData)
      val encoder = encoderFactory.jsonEncoder(
        writerSchema,
        this,
        true
      )

      writer.write(record, encoder)
      encoder.flush()
    }

    return JsonString(jsonBytes.toUtf8String())
  }
}
