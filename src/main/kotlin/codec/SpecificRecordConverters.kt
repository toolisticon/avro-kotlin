package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.codec.AvroCodec.Converter
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase

object SpecificRecordConverters {

  fun <T : SpecificRecordBase> avroSchema(recordType: Class<T>): AvroSchema = AvroSchema(schema = recordType.getDeclaredField("SCHEMA\$").get(null) as Schema)


  @Suppress("KotlinConstantConditions")
  fun <T : SpecificRecordBase> specificToGenericRecord(genericData: GenericData = AvroCodec.defaultGenericData) =
    Converter<T, GenericData.Record> { record ->
      genericData.deepCopy(record.schema, record) as GenericData.Record
    }

  inline fun <reified T : SpecificRecordBase> genericToSpecificRecord(schema: AvroSchema = avroSchema(T::class.java)) =
    Converter<GenericData.Record, T> { record ->
      val data = T::class.java.getDeclaredField("MODEL\$").apply { isAccessible = true }.get(null) as SpecificData
      data.deepCopy(schema.get(), record) as T
    }
}
