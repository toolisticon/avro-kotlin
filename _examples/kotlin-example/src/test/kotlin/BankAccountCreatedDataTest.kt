package io.toolisticon.kotlin.avro.example

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.example.KotlinExample.avro
import io.toolisticon.kotlin.avro.example.customerid.CustomerId
import io.toolisticon.kotlin.avro.example.money.MoneyLogicalType
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.value.CanonicalName.Companion.toCanonicalName
import io.toolisticon.kotlin.avro.value.Name.Companion.toName
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import java.util.*

internal class BankAccountCreatedDataTest {

  @Test
  fun `show schema`() {
    val schema = avro.schema(BankAccountCreatedData::class)
    assertThat(schema.canonicalName).isEqualTo("io.toolisticon.bank.BankAccountCreated".toCanonicalName())
    assertThat(schema.fields).hasSize(3)

    val props = schema.getField("initialBalance")?.properties
    assertThat(schema.getField("initialBalance".toName())?.schema?.logicalTypeName).isEqualTo(MoneyLogicalType.name)
  }

  @Test
  fun `serialize data class with uuid customerId and money to generic record and back`() {
    val amount = Money.of(10, "EUR")
    val accountId = UUID.randomUUID()
    val customerId = CustomerId.random()

    val event = BankAccountCreatedData(accountId, customerId, amount)
    val record = avro.encodeToGenericRecord(event)

    assertThat(avro.decodeFromGenericRecord(record, BankAccountCreatedData::class)).isEqualTo(event)
  }

  @Test
  fun `serialize data class with uuid customerId and money to record and single object encoded and back`() {
    val amount = Money.of(10, "EUR")
    val accountId = UUID.randomUUID()
    val customerId = CustomerId.random()

    val event = BankAccountCreatedData(accountId, customerId, amount)
    val resolver = AvroKotlin.avroSchemaResolver(avro.schema(BankAccountCreatedData::class))

    val record = avro.encodeToGenericRecord(event)

    assertThat(avro.decodeFromGenericRecord(record, BankAccountCreatedData::class)).isEqualTo(event)

    // go down and run through SOE

    val singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(record)
    val recordDecodedFromBytes = GenericRecordCodec.decodeSingleObject(singleObjectEncodedBytes, resolver.invoke())
    val deserializedEvent = AvroKotlinSerialization().decodeFromGenericRecord<BankAccountCreatedData>(recordDecodedFromBytes)
    assertThat(deserializedEvent).isEqualTo(event)
  }

  @Test
  fun `serialize single object encoded`() {
    val amount = Money.of(10, "EUR")
    val accountId = UUID.randomUUID()
    val customerId = CustomerId.random()

    val event = BankAccountCreatedData(accountId, customerId, amount)

    val soeb = avro.encodeToSingleObjectEncoded(event)

    val decoded = avro.decodeFromSingleObjectEncoded(soeb, BankAccountCreatedData::class)
    assertThat(decoded).isEqualTo(event)
  }
}
