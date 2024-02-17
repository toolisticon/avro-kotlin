package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.createGenericRecord
import io.toolisticon.avro.kotlin.AvroSchemaStore
import io.toolisticon.avro.kotlin._test.MoneyLogicalType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.LogicalTypes
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test

internal class MoneyLogicalTypeTest {

  private val writerSchema = AvroSchema(
    SchemaBuilder.record("foo.MoneyData")
      .fields()
      .name("money")
      .type(MoneyLogicalType.INSTANCE.conversion.recommendedSchema)
      .noDefault()
      .endRecord()
  )

  @Test
  fun `money logicalType is loaded via SPI`() {
    // we have money present
    assertThat(LogicalTypes.getCustomRegisteredTypes()["money"]).isNotNull
  }

  @Test
  fun `read and write generic record with money`() {

    val amount = Money.of(100.3456, "EUR")
    val record = createGenericRecord(writerSchema) {
      put("money", amount)
    }

    val json = AvroKotlin.genericRecordToJson(record)

    val recordFromJson = AvroKotlin.genericRecordFromJson(json, writerSchema)

    assertThat(recordFromJson.get("money")).isEqualTo(amount)
  }

  @Test
  fun `read and write generic record with money to singleObjectEncoded bytes using schemaStore`() {
    val amount = Money.of(100.3456, "EUR")
    val record = createGenericRecord(writerSchema) {
      put("money", amount)
    }

    val schemaStore = AvroSchemaStore { _ -> writerSchema }

    val bytes = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    val recordFromBytes = AvroKotlin.genericRecordFromSingleObjectEncoded(
      singleObjectEncodedBytes = bytes,
      readerSchema = writerSchema,
      schemaStore
    )

    assertThat(recordFromBytes.get("money")).isEqualTo(amount)
  }


  @Test
  fun `read and write generic record with money to singleObjectEncoded bytes`() {
    val amount = Money.of(100.3456, "EUR")
    val record = createGenericRecord(writerSchema) {
      put("money", amount)
    }

    val bytes = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    val recordFromBytes = AvroKotlin.genericRecordFromPayloadBytes(
      payloadBytes = bytes.payload,
      readerSchema = writerSchema,
    )
    assertThat(recordFromBytes.get("money")).isEqualTo(amount)
  }
}
