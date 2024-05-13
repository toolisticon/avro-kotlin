package io.toolisticon.avro.kotlin.example

import io.toolisticon.avro.kotlin.example.money.MoneyLogicalType
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BankAccountCreatedTest {

  @Test
  fun `show schema`() {
    val avroSerialization = AvroKotlinSerialization()

    val schema = avroSerialization.schema(BankAccountCreated::class)

    println(schema.toString(true))

    assertThat(schema.canonicalName).isEqualTo(CanonicalName(BankAccountCreated::class.qualifiedName!!))
    assertThat(schema.fields).hasSize(2)

    val props = schema.getField("initialBalance")?.properties


    assertThat(schema.getField(Name("initialBalance"))?.schema?.logicalTypeName).isEqualTo(MoneyLogicalType.name)
  }
}
