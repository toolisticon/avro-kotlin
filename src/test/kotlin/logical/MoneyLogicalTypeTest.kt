package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin._test.MoneyLogicalType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.LogicalTypes
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test

internal class MoneyLogicalTypeTest {

  @Test
  fun `money logicalType is loaded via SPI`() {
    assertThat(LogicalTypes.getCustomRegisteredTypes()).hasSize(1)
    assertThat(LogicalTypes.getCustomRegisteredTypes()["money"]).isNotNull
  }

  @Test
  fun `write generic record with money`() {
    val schema = SchemaBuilder.record("foo.MoneyData")
      .fields()
      .name("money")
      .type(MoneyLogicalType.INSTANCE.conversion.recommendedSchema)
      .noDefault()
      .endRecord()

    val amount = Money.of(100.3456, "EUR")
    val record = AvroKotlin.createGenericRecord(AvroSchema(schema)) {
      put("money", amount)
    }

    val json = AvroKotlin.genericRecordToJson(record)

    val recordFromJson = AvroKotlin.genericRecordFromJson(json, AvroSchema(schema))

    assertThat(recordFromJson.get("money")).isEqualTo(amount)
  }
}
