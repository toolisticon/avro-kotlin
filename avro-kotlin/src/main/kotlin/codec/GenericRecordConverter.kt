package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.repository.AvroSchemaResolver
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord

/**
 * Takes a [GenericRecord] and applies a new schema to it.
 *
 * Currently, this uses binary encoding/decoding.
 *
 * TODO: Would be nice if we could achieve this without encoding, see [so-question](https://stackoverflow.com/questions/77879245/avro-schema-evolution-update-schema-of-avro-generic-record-via-copy-deepcopy-r).
 */
class GenericRecordConverter private constructor(
    private val readerSchemaSupplier: AvroSchemaResolver,
    private val genericData: GenericData
) : AvroCodec.Converter<GenericRecord, GenericRecord> {

  constructor(
    readerSchema: AvroSchema,
    genericData: GenericData = AvroKotlin.genericData
  ) : this(
    readerSchemaSupplier = { readerSchema },
    genericData = genericData
  )

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun convert(record: GenericRecord): GenericRecord {
    val readerSchema = readerSchemaSupplier().get()
    val writerSchema = record.schema

    val bytes = GenericRecordCodec.encodeByteArray(record, genericData)
    return GenericRecordCodec.decodeByteArray(bytes, readerSchema, writerSchema, genericData)
  }
}
