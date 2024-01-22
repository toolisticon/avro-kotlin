package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
import org.apache.avro.message.SchemaStore.Cache
import org.apache.avro.util.Utf8
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

data class FooString(
  val str: String
) {
  companion object {

    const val SINGLE_OBJECT_BAR = "[C3 01 1D 6C 12 78 03 3B 7C A0 06 62 61 72]"

    fun of(record: GenericData.Record): FooString = FooString(str = (record.get("str") as Utf8).toString() )

    val SCHEMA: AvroSchema = AvroSchema(
      SchemaBuilder.record("io.toolisticon.avro.kotlin._test.FooString")
        .fields()
        .requiredString("str")
        .endRecord()
    )

    fun genericRecord(fooString: FooString): GenericData.Record = AvroKotlin.createGenericRecord(SCHEMA) {
      put("str", fooString.str)
    }
  }
}

internal class FooStringTest {

  @Test
  fun `has correct fingerprint`() {
    val cache = Cache().apply {
      addSchema(FooString.SCHEMA.get())
    }

    val bar = FooString("bar")
    val record = FooString.genericRecord(bar)

    assertThat(AvroSchema(record.schema)).isEqualTo(FooString.SCHEMA)

    val encoded = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    assertThat(encoded.fingerprint).isEqualTo(FooString.SCHEMA.fingerprint)

    val decoded = AvroKotlin.genericRecordFromSingleObjectEncoded(encoded, FooString.SCHEMA, AvroKotlin.schemaStore(cache))

    assertThat(FooString.of(decoded)).isEqualTo(bar)

  }
}
