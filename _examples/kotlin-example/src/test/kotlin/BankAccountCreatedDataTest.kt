package io.toolisticon.kotlin.avro.example

import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.example.customerid.CustomerId
import io.toolisticon.kotlin.avro.example.money.MoneyLogicalType
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
import io.toolisticon.kotlin.avro.value.CanonicalName.Companion.toCanonicalName
import io.toolisticon.kotlin.avro.value.Name.Companion.toName
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
    assertThat(schema.getField("initialBalance".toName())?.schema?.logicalTypeName).isEqualTo(MoneyLogicalType.name)
  }

  @Test
  fun `serialize single object with uuid customerId and money`() {
    val amount = Money.of(10, "EUR")
    val accountId = UUID.randomUUID()
    val customerId = CustomerId.random()

    val event = BankAccountCreatedData(accountId, customerId, amount)
    val resolver = avroSchemaResolver(KotlinExample.avro.schema(BankAccountCreatedData::class))

    val record = KotlinExample.avro.toRecord(event)

    val json = GenericRecordCodec.encodeJson(record)

    val decodedRecord = GenericRecordCodec.decodeJson(json, KotlinExample.avro.schema(BankAccountCreatedData::class))

    assertThat(KotlinExample.avro.fromRecord(record, BankAccountCreatedData::class)).isEqualTo(event)
  }
}
