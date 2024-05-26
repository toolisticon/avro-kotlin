package io.toolisticon.kotlin.avro._test

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.builder.AvroBuilder.uuid
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import java.util.*


data class BankAccountCreatedData(
  val bankAccountId: UUID,
  val initialBalance: Int
) : ToGenericRecord {
  companion object {
    val SCHEMA = AvroSchema(
      SchemaBuilder.record("lib.test.event.BankAccountCreated")
        .fields()
        .uuid("bankAccountId")
        .requiredInt("initialBalance")
        .endRecord()
    )

    operator fun invoke(record: GenericRecord): BankAccountCreatedData = BankAccountCreatedData(
      bankAccountId = record.get("bankAccountId") as UUID,
      initialBalance = record.get("initialBalance") as Int
    )
  }

  override fun toGenericRecord(): GenericRecord = AvroKotlin.createGenericRecord(SCHEMA) {
    put("bankAccountId", bankAccountId)
    put("initialBalance", initialBalance)
  }
}
