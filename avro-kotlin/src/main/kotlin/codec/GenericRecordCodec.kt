package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.codec.AvroCodec.decoderFactory
import io.toolisticon.kotlin.avro.codec.AvroCodec.encoderFactory
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import java.io.ByteArrayOutputStream

object GenericRecordCodec {

  internal fun encodeByteArray(
    record: GenericRecord,
    genericData: GenericData = AvroKotlin.genericData
  ): ByteArray {
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
    genericData: GenericData = AvroKotlin.genericData
  ): GenericRecord {
    return GenericDatumReader<GenericRecord>(
      writerSchema,
      readerSchema,
      genericData
    ).read(null, decoderFactory.binaryDecoder(bytes, null))
  }

  @JvmStatic
  fun convert(
    record: GenericRecord,
    readerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericData
  ) = GenericRecordConverter(
    readerSchema = readerSchema,
    genericData = genericData
  ).convert(record)

  @JvmStatic
  fun convert(
      singleObjectEncodedBytes: SingleObjectEncodedBytes,
      avroSchemaResolver: AvroSchemaResolver,
      genericData: GenericData = AvroKotlin.genericData
  ) = SingleObjectToJsonConverter(
    avroSchemaResolver = avroSchemaResolver,
    genericData = genericData
  ).convert(singleObjectEncodedBytes)

  @JvmStatic
  fun encodeJson(
    record: GenericRecord,
    genericData: GenericData = AvroKotlin.genericData
  ): JsonString = GenericRecordJsonEncoder(genericData)
    .encode(record)

  @JvmStatic
  fun decodeJson(
    json: JsonString,
    readerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericData
  ) = GenericRecordJsonDecoder(readerSchema, genericData)
    .decode(json)

  @JvmStatic
  fun encodeSingleObject(
    record: GenericRecord,
    genericData: GenericData = AvroKotlin.genericData
  ) = GenericRecordSingleObjectEncoder(genericData)
    .encode(record)

  @JvmStatic
  fun decodeSingleObject(
    singleObjectEncodedBytes: SingleObjectEncodedBytes,
    readerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericData
  ): GenericRecord = GenericRecordSingleObjectDecoder(
    readerSchema = readerSchema,
    genericData = genericData
  ).decode(singleObjectEncodedBytes)
}
