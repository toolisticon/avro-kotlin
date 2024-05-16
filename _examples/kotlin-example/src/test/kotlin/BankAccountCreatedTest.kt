package io.toolisticon.avro.kotlin.example

import io.toolisticon.avro.kotlin.avroSchemaResolver
import io.toolisticon.avro.kotlin.codec.GenericRecordCodec
import io.toolisticon.avro.kotlin.example.customerid.CustomerId
import io.toolisticon.avro.kotlin.example.money.MoneyLogicalType
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import java.util.*

internal class BankAccountCreatedTest {

  @Test
  fun `show schema`() {
    val schema = KotlinExample.avro.schema(BankAccountCreated::class)

    assertThat(schema.canonicalName).isEqualTo(CanonicalName(BankAccountCreated::class.qualifiedName!!))
    assertThat(schema.fields).hasSize(3)

    val props = schema.getField("initialBalance")?.properties
    assertThat(schema.getField(Name("initialBalance"))?.schema?.logicalTypeName).isEqualTo(MoneyLogicalType.name)
  }

  @Test
  fun `serialize single object with uuid customerId and money`() {
    val amount = Money.of(10, "EUR")
    val accountId = UUID.randomUUID()
    val customerId = CustomerId.random()

    val event = BankAccountCreated(accountId, customerId, amount)
    val resolver = avroSchemaResolver(KotlinExample.avro.schema(BankAccountCreated::class))

    val record = KotlinExample.avro.toRecord(event)

    val json = GenericRecordCodec.encodeJson(record)

    val decodedRecord = GenericRecordCodec.decodeJson(json, KotlinExample.avro.schema(BankAccountCreated::class))

    assertThat(KotlinExample.avro.fromRecord(record, BankAccountCreated::class)).isEqualTo(event)
  }
}
