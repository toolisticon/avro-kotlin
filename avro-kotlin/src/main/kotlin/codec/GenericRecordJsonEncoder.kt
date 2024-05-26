package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.codec.AvroCodec.JsonEncoder
import io.toolisticon.kotlin.avro.codec.AvroCodec.encoderFactory
import io.toolisticon.kotlin.avro.value.ByteArrayValue
import io.toolisticon.kotlin.avro.value.JsonString
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

    return JsonString.of(jsonBytes.toUtf8String())
  }
}
