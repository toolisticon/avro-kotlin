package io.toolisticon.avro.kotlin.example

import io.toolisticon.avro.kotlin.codec.GenericRecordCodec
import io.toolisticon.avro.kotlin.example.customerid.CustomerId
import io.toolisticon.avro.kotlin.example.customerid.CustomerIdData
import io.toolisticon.avro.kotlin.example.money.MoneyLogicalType
import io.toolisticon.avro.kotlin.repository.avroSchemaResolver
import io.toolisticon.avro.kotlin.value.CanonicalName.Companion.toCanonicalName
import io.toolisticon.avro.kotlin.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import java.util.*

internal class BankAccountCreatedDataTest {

  @Test
  fun `show schema`() {
    val schema = KotlinExample.avro.schema(BankAccountCreatedData::class)
    println(schema)
    assertThat(schema.canonicalName).isEqualTo("io.toolisticon.bank.BankAccountCreated".toCanonicalName())
    assertThat(schema.fields).hasSize(3)

    val props = schema.getField("initialBalance")?.properties
    assertThat(schema.getField(Name("initialBalance"))?.schema?.logicalTypeName).isEqualTo(MoneyLogicalType.name)
  }

  @Test
  fun `serialize single object with uuid customerId and money`() {
    val amount = Money.of(10, "EUR")
    val accountId = UUID.randomUUID()
    val customerId = CustomerIdData("1")

    val event = BankAccountCreatedData(accountId, customerId, amount)
    val resolver = avroSchemaResolver(KotlinExample.avro.schema(BankAccountCreatedData::class))

    val record = KotlinExample.avro.toRecord(event)

    val json = GenericRecordCodec.encodeJson(record)

    val decodedRecord = GenericRecordCodec.decodeJson(json, KotlinExample.avro.schema(BankAccountCreatedData::class))

    assertThat(KotlinExample.avro.fromRecord(record, BankAccountCreatedData::class)).isEqualTo(event)
  }
}
