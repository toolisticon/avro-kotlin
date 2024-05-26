package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolver
import io.toolisticon.kotlin.avro.codec.AvroCodec.JsonDecoder
import io.toolisticon.kotlin.avro.codec.AvroCodec.decoderFactory
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.JsonString
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
