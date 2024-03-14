package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.builder.AvroBuilder.uuid
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.HexString
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
import org.apache.avro.util.Utf8
import java.util.*

interface ToGenericRecord {
  fun toGenericRecord(): GenericData.Record
}

/**
 * An example data class with one string field .
 */
data class FooString(
  val str: String
) : ToGenericRecord {
  companion object {

    val SINGLE_OBJECT_BAR = HexString("[C3 01 1D 6C 12 78 03 3B 7C A0 06 62 61 72]")

    val SCHEMA: AvroSchema = AvroSchema(
      SchemaBuilder.record("io.toolisticon.avro.kotlin._test.FooString")
        .fields()
        .requiredString("str")
        .endRecord()
    )

    operator fun invoke(record: GenericData.Record): FooString = FooString(str = (record.get("str") as Utf8).toString())
  }

  override fun toGenericRecord(): GenericData.Record = AvroKotlin.createGenericRecord(SCHEMA) {
    put("str", str)
  }
}

/**
 * Like FooString, but with optional UUID field
 */
data class FooString2(
  val str: String,
  val uuid: UUID?
) : ToGenericRecord {
  companion object {
    val SCHEMA = AvroSchema(
      SchemaBuilder.record("io.toolisticon.avro.kotlin._test.FooString")
        .fields()
        .requiredString("str")
        .uuid("uuid", true)
        .endRecord()
    )

    operator fun invoke(record: GenericData.Record): FooString2 = FooString2(
      str = (record.get("str") as Utf8).toString(),
      uuid = record.get("uuid") as UUID?
    )
  }

  override fun toGenericRecord() = AvroKotlin.createGenericRecord(SCHEMA) {
    put("str", str)
    put("uuid", uuid)
  }
}

