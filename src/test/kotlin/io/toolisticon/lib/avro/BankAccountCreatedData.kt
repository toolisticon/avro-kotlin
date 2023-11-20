package io.toolisticon.lib.avro

import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData.SchemaConstructable
import org.apache.avro.specific.SpecificRecord
import java.util.*

data class BankAccountCreatedData(
    val bankAccountId: UUID,
    val initialBalance: Int
) : SpecificRecord,SchemaConstructable {

  constructor(schema: Schema) : this(bankAccountId = UUID.randomUUID(), initialBalance = 18)

  override fun getSchema(): Schema {
    TODO("getSchema - Not yet implemented")
  }

  override fun put(i: Int, v: Any?) {
    TODO("put - Not yet implemented")
  }

  override fun get(i: Int): Any {
    TODO("get - Not yet implemented")
  }

}
