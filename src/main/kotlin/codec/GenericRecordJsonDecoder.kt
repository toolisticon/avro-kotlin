package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin.defaultLogicalTypeConversions
import io.toolisticon.avro.kotlin.AvroSchemaResolver
import io.toolisticon.avro.kotlin.codec.AvroCodec.JsonDecoder
import io.toolisticon.avro.kotlin.codec.AvroCodec.decoderFactory
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.JsonString
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GenericRecordJsonDecoder private constructor(
  private val readerSchemaSupplier: AvroSchemaResolver,
  private val writerSchemaSupplier: AvroSchemaResolver,
  private val genericData: GenericData,
) : JsonDecoder<GenericData.Record> {

  constructor(
    readerSchema: AvroSchema,
    genericData: GenericData = defaultLogicalTypeConversions.genericData
  ) : this(
    readerSchemaSupplier = { readerSchema },
    writerSchemaSupplier = { readerSchema },
    genericData = genericData
  )

  constructor(
    readerSchema: AvroSchema,
    writerSchema: AvroSchema,
    genericData: GenericData = defaultLogicalTypeConversions.genericData
  ) : this(
    readerSchemaSupplier = { readerSchema },
    writerSchemaSupplier = { writerSchema },
    genericData = genericData
  )

  override fun decode(json: JsonString): GenericData.Record {
    val readerSchema = readerSchemaSupplier().get()
    val writerSchema = writerSchemaSupplier().get()

    val jsonDecoder: org.apache.avro.io.JsonDecoder = decoderFactory.jsonDecoder(
      readerSchema,
      json.inputStream()
    )
    val decoder = decoderFactory.validatingDecoder(readerSchema, jsonDecoder)

    val reader = GenericDatumReader<GenericData.Record>(
      writerSchema,
      readerSchema,
      genericData
    )

    return reader.read(null, decoder)
  }
}
