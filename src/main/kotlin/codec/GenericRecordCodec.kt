package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.codec.AvroCodec.decoderFactory
import io.toolisticon.avro.kotlin.codec.AvroCodec.defaultGenericData
import io.toolisticon.avro.kotlin.codec.AvroCodec.encoderFactory
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import java.io.ByteArrayOutputStream

object GenericRecordCodec {

  internal fun encodeByteArray(record: GenericData.Record, genericData: GenericData = defaultGenericData): ByteArray {
    val writerSchema: Schema = record.schema

    return ByteArrayOutputStream().use { baos ->
      val writer = GenericDatumWriter<Any>(writerSchema, genericData)
      val encoder = encoderFactory.binaryEncoder(baos, null)
      writer.write(record, encoder)
      encoder.flush()
      baos.toByteArray()
    }
  }

  internal fun decodeByteArray(
    bytes: ByteArray,
    readerSchema: Schema,
    writerSchema: Schema,
    genericData: GenericData = defaultGenericData
  ): GenericData.Record {
    return GenericDatumReader<GenericData.Record>(
      writerSchema,
      readerSchema,
      genericData
    ).read(null, decoderFactory.binaryDecoder(bytes, null))
  }

  fun convert(record: GenericData.Record, readerSchema: AvroSchema, genericData: GenericData = defaultGenericData) = GenericRecordConverter(
    readerSchema = readerSchema,
    genericData = genericData
  ).convert(record)

  fun encodeJson(
    record: GenericData.Record,
    genericData: GenericData = defaultGenericData
  ): JsonString =
    GenericRecordJsonEncoder(genericData).encode(record)

  fun decodeJson(
    json: JsonString,
    readerSchema: AvroSchema,
    genericData: GenericData = defaultGenericData
  ) = GenericRecordJsonDecoder(readerSchema, genericData).decode(json)

  fun encodeSingleObject(
    record: GenericData.Record,
    genericData: GenericData = AvroKotlin.genericDataWithConversions
  ) = GenericRecordSingleObjectEncoder(genericData).encode(
    record
  )

  fun decodeSingleObject(
    singleObjectEncodedBytes: SingleObjectEncodedBytes,
    readerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericDataWithConversions
  ): GenericData.Record = GenericRecordSingleObjectDecoder(
    readerSchema = readerSchema,
    genericData = genericData
  ).decode(singleObjectEncodedBytes)
}
