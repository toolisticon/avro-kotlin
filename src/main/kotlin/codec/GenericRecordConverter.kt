package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.generic.GenericData

/**
 * Takes a [GenericData.Record] and applies a new schema to it.
 *
 * Currently, this uses binary encoding/decoding.
 *
 * TODO: Would be nice if we could achieve this without encoding, see [so-question](https://stackoverflow.com/questions/77879245/avro-schema-evolution-update-schema-of-avro-generic-record-via-copy-deepcopy-r).
 */
class GenericRecordConverter private constructor(
  private val readerSchemaSupplier: AvroCodec.AvroSchemaSupplier,
  private val genericData: GenericData
) : AvroCodec.Converter<GenericData.Record, GenericData.Record> {

  constructor(
    readerSchema: AvroSchema,
    genericData: GenericData = AvroCodec.defaultGenericData
  ) : this(
    readerSchemaSupplier = { readerSchema },
    genericData = genericData
  )

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun convert(record: GenericData.Record): GenericData.Record {
    val readerSchema = readerSchemaSupplier().get()
    val writerSchema = record.schema

    val bytes = GenericRecordCodec.encodeByteArray(record, genericData)
    return GenericRecordCodec.decodeByteArray(bytes, readerSchema, writerSchema, genericData)
  }
}
