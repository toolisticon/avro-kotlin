package io.toolisticon.kotlin.avro.example

import io.toolisticon.example.bank.BankAccountCreated
import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.example.customerid.CustomerId
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*

@Disabled("fails because schemas are not identical.")
internal class JavaKotlinInterOpTest {

  private val avro = AvroKotlinSerialization()

  @Test
  fun `schema are identical`() {
    val javaSchema = AvroSchema(BankAccountCreated.getClassSchema())
    val kotlinSchema = avro.schema(BankAccountCreatedData::class)

    println(javaSchema)
    println(kotlinSchema)

    assertThat(javaSchema.fingerprint).isEqualTo(kotlinSchema.fingerprint)
  }

  @Test
  fun `serialize from java deserialize kotlin`() {
    val orig = BankAccountCreated.newBuilder()
      .setAccountId(UUID.randomUUID())
      .setCustomerId(CustomerId.random().id)
      .setInitialBalance(Money.of(100, "EUR"))
      .build()

    val store = avroSchemaResolver(
      avro.schema(BankAccountCreatedData::class),
      AvroSchema(BankAccountCreated.`SCHEMA$`)
    )

    val bytes = SingleObjectEncodedBytes.of(BankAccountCreated.getEncoder().encode(orig))

    val record = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = bytes,
      readerSchema = avro.schema(BankAccountCreatedData::class)
    )

    val decoded = avro.fromGenericRecord(record, BankAccountCreatedData::class)

    assertThat(decoded.accountId).isEqualTo(orig.accountId)
    assertThat(decoded.customerId).isEqualTo(orig.customerId)
    assertThat(decoded.initialBalance).isEqualTo(orig.initialBalance)
  }
}
