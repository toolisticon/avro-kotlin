package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroSchemaResolver
import io.toolisticon.avro.kotlin.codec.AvroCodec.JsonDecoder
import io.toolisticon.avro.kotlin.codec.AvroCodec.decoderFactory
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.JsonString
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GenericRecordJsonDecoder private constructor(
  private val readerSchemaSupplier: AvroSchemaResolver,
  private val writerSchemaSupplier: AvroSchemaResolver,
  private val genericData: GenericData,
) : JsonDecoder<GenericRecord> {

  constructor(
    readerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericData
  ) : this(
    readerSchemaSupplier = { readerSchema },
    writerSchemaSupplier = { readerSchema },
    genericData = genericData
  )

  constructor(
    readerSchema: AvroSchema,
    writerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericData
  ) : this(
    readerSchemaSupplier = { readerSchema },
    writerSchemaSupplier = { writerSchema },
    genericData = genericData
  )

  override fun decode(json: JsonString): GenericRecord {
    val readerSchema = readerSchemaSupplier().get()
    val writerSchema = writerSchemaSupplier().get()

    val jsonDecoder: org.apache.avro.io.JsonDecoder = decoderFactory.jsonDecoder(
      readerSchema,
      json.inputStream()
    )
    val decoder = decoderFactory.validatingDecoder(readerSchema, jsonDecoder)

    val reader = GenericDatumReader<GenericRecord>(
      writerSchema,
      readerSchema,
      genericData
    )

    return reader.read(null, decoder)
  }
}
