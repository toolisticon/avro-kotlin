package io.toolisticon.kotlin.avro.codec

import io.toolisticon.kotlin.avro.TestFixtures.BankAccountCreatedFixtures
import io.toolisticon.kotlin.avro._test.BankAccountCreatedData
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
import lib.test.event.BankAccountCreated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class SpecificRecordCodecTest {

  private val bankAccountId = UUID.randomUUID()
  private val initialBalance = 66

  @Test
  fun `encode and decode bankAccountCreated`() {
    val original = BankAccountCreatedFixtures.bankAccountCreated

    val store = avroSchemaResolver(BankAccountCreatedFixtures.SCHEMA_BANK_ACCOUNT_CREATED)

    val encoded = SpecificRecordCodec.encodeSingleObject(original)

    val decoded = SpecificRecordCodec.decodeSingleObject<BankAccountCreated>(bytes = encoded, schemaResolver = store)

    assertThat(decoded).isEqualTo(original)
  }

  @Test
  fun `convert generic record to specific`() {
    val converter = SpecificRecordCodec.genericRecordToSpecificRecordConverter()
    val generic = BankAccountCreatedData(bankAccountId, initialBalance).toGenericRecord()

    val specific = converter.convert(generic)

    assertThat(specific).isInstanceOf(BankAccountCreated::class.java)

    with(specific as BankAccountCreated) {
      assertThat(this.bankAccountId).isEqualTo(bankAccountId)
      assertThat(this.initialBalance).isEqualTo(initialBalance)
    }
  }

  @Test
  fun `convert specific record to generic`() {
    val converter = SpecificRecordCodec.specificRecordToGenericRecordConverter()
    val specific = BankAccountCreated.newBuilder()
      .setBankAccountId(bankAccountId)
      .setInitialBalance(initialBalance)
      .build()

    val generic = converter.convert(specific)
    val data = BankAccountCreatedData(generic)

    assertThat(data.bankAccountId).isEqualTo(bankAccountId)
    assertThat(data.initialBalance).isEqualTo(initialBalance)
  }

  @Test
  fun `decode singleObjectEncoded bankAccountCreated`() {
    val encoded = BankAccountCreatedFixtures.singleObjectEncoded
    val schemaResolver = avroSchemaResolver(BankAccountCreatedFixtures.SCHEMA_BANK_ACCOUNT_CREATED)
    val decoder = SpecificRecordCodec.specificRecordSingleObjectDecoder(schemaResolver)

    val decoded = decoder.decode(encoded)

    assertThat(decoded).isEqualTo(BankAccountCreatedFixtures.bankAccountCreated)
  }

  @Test
  fun `encode bankAccountCreated`() {
    val original = BankAccountCreatedFixtures.bankAccountCreated

    val encoder = SpecificRecordCodec.specificRecordSingleObjectEncoder()

    val encoded = encoder.encode(original)

    assertThat(encoded.hex).isEqualTo(BankAccountCreatedFixtures.hexString)
  }
}
