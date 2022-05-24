package TestFixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.toolisticon.lib.avro.AvroKotlinLib

internal object TestFixtures {
  const val SCHEMA_ROOT = "avro"

  val OM = jacksonObjectMapper().findAndRegisterModules()

  val fqnBankAccountCreated = AvroKotlinLib.fqn("lib.test.event", "BankAccountCreated")
  val fqnFindCurrentBalance = AvroKotlinLib.fqn("lib.test.query", "FindCurrentBalance")


}
