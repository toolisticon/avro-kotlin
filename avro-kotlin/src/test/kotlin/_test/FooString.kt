package io.toolisticon.kotlin.avro._test

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.builder.AvroBuilder.uuid
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.HexString
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericRecord
import org.apache.avro.util.Utf8
import java.util.*

interface ToGenericRecord {
  fun toGenericRecord(): GenericRecord
}

/**
 * An example data class with one string field .
 */
data class FooString(
  val str: String
) : ToGenericRecord {
  companion object {

    val SINGLE_OBJECT_BAR = HexString.parse("[C3 01 68 2F CD 81 41 03 69 8A 06 62 61 72]")

    val SCHEMA: AvroSchema = AvroSchema(
      SchemaBuilder.record("io.toolisticon.kotlin.avro._test.FooString")
        .fields()
        .requiredString("str")
        .endRecord()
    )

    operator fun invoke(record: GenericRecord): FooString = FooString(str = (record.get("str") as Utf8).toString())
  }

  override fun toGenericRecord(): GenericRecord = AvroKotlin.createGenericRecord(SCHEMA) {
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
      SchemaBuilder.record("io.toolisticon.kotlin.avro._test.FooString")
        .fields()
        .requiredString("str")
        .uuid("uuid", true)
        .endRecord()
    )

    operator fun invoke(record: GenericRecord): FooString2 = FooString2(
      str = (record.get("str") as Utf8).toString(),
      uuid = record.get("uuid") as UUID?
    )
  }

  override fun toGenericRecord() = AvroKotlin.createGenericRecord(SCHEMA) {
    put("str", str)
    put("uuid", uuid)
  }
}

