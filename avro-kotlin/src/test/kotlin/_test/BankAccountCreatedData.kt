package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.builder.AvroBuilder.uuid
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
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

    operator fun invoke(record: GenericData.Record): BankAccountCreatedData = BankAccountCreatedData(
      bankAccountId = record.get("bankAccountId") as UUID,
      initialBalance = record.get("initialBalance") as Int
    )
  }

  override fun toGenericRecord(): GenericData.Record = AvroKotlin.createGenericRecord(SCHEMA) {
    put("bankAccountId", bankAccountId)
    put("initialBalance", initialBalance)
  }
}
