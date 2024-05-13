package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecord

@JvmInline
value class AvroRecord private constructor(
  override val value: Pair<AvroSchema, GenericData.Record>
) : PairType<AvroSchema, GenericData.Record>, GenericRecord, SpecificRecord {

  constructor(record: GenericData.Record) : this(AvroSchema(record.schema) to record)

  override fun getSchema(): Schema = value.first.get()

  override fun put(key: String?, v: Any?) = throw UnsupportedOperationException("This implementation of Record is immutable")

  override fun put(i: Int, v: Any?) = throw UnsupportedOperationException("This implementation of Record is immutable")

  override fun get(key: String): Any = value.second.get(key)

  override fun get(index: Int): Any = value.second.get(index)

  override fun toString() = value.second.toString()
}
