package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin._test.BankAccountCreatedData
import lib.test.event.BankAccountCreated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*


internal class SpecificRecordConvertersTest {

  private val toGenericConverter = SpecificRecordConverters.specificToGenericRecord<BankAccountCreated>()
  private val toSpecificConverter = SpecificRecordConverters.genericToSpecificRecord<BankAccountCreated>()


  private val kotlin = BankAccountCreatedData(bankAccountId = UUID.randomUUID(), initialBalance = 100)
  private val java = BankAccountCreated.newBuilder().setBankAccountId(kotlin.bankAccountId).setInitialBalance(kotlin.initialBalance).build()

  @Test
  fun `specific record to generic record`() {
    val generic = toGenericConverter.convert(java)

    assertThat(BankAccountCreatedData(generic)).isEqualTo(kotlin)
  }

  @Test
  fun `generic record to specific record`() {
    val generic = kotlin.toGenericRecord()

    assertThat(toSpecificConverter.convert(generic)).isEqualTo(java)
  }
}
