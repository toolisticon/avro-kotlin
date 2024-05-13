package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.avroSchemaResolver
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.compatibleToBeReadFrom
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.compatibleToReadFrom
import org.apache.avro.SchemaCompatibility
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class FooStringTest {

  @Test
  fun `has correct fingerprint`() {
    val cache = avroSchemaResolver(FooString.SCHEMA)

    val bar = FooString("bar")
    val record = bar.toGenericRecord()

    assertThat(AvroSchema(record.schema)).isEqualTo(FooString.SCHEMA)

    val encoded = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    assertThat(encoded.fingerprint).isEqualTo(FooString.SCHEMA.fingerprint)

    val decoded =
      AvroKotlin.genericRecordFromSingleObjectEncoded(encoded, FooString.SCHEMA, cache)

    assertThat(FooString(decoded)).isEqualTo(bar)

  }

  @Test
  fun `check compatibility`() {
    val decode = FooString.SCHEMA.compatibleToReadFrom(FooString2.SCHEMA)
    val encode = FooString.SCHEMA.compatibleToBeReadFrom(FooString2.SCHEMA)

    assertThat(decode.result.compatibility).isEqualTo(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE)
    assertThat(encode.result.compatibility).isEqualTo(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE)
  }


  @Test
  @Disabled("still looking for a way to convert schema on record without coded")
  fun `can convert fooString to fooString2`() {
    val data = FooString("bar")
    val record = data.toGenericRecord()


    assertThat(AvroKotlin.defaultLogicalTypeConversions.genericData.validate(record.schema, record)).isTrue()

    val record2: GenericData.Record = AvroKotlin.defaultLogicalTypeConversions.genericData.newRecord(
      record,
      FooString2.SCHEMA.get(),
    ) as GenericData.Record

    GenericData.Record(record, true)

    assertThat(AvroKotlin.defaultLogicalTypeConversions.genericData.validate(record2.schema, record2)).isFalse()
  }
}
