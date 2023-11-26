package io.toolisticon.avro.kotlin._test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder

internal object TestFixtures {
  const val SCHEMA_ROOT = "avro"

  val OM = jacksonObjectMapper().findAndRegisterModules()

  val fqnBankAccountCreated = AvroKotlin.fqn(Namespace("lib.test.event"), Name("BankAccountCreated"))
  val fqnFindCurrentBalance = AvroKotlin.fqn(Namespace("lib.test.query"), Name("FindCurrentBalance"))

  /**
   * A simple schema containing only one string `value`.
   */
  val schemaFoo = simpleStringValueSchema(Namespace("com.acme.test"), Name("Foo"))

  /**
   * A simple schema containing only one string `value`.
   */
  val schemaBar = simpleStringValueSchema(Namespace("com.acme.dummy"), Name("Bar"))

  fun simpleStringValueSchema(namespace: Namespace, name: Name) = SchemaBuilder.record(name.value)
    .namespace(namespace.value)
    .doc("some test")
    .fields()
    .name("value")
    .type(Schema.create(Schema.Type.STRING))
    .noDefault()
    .endRecord()
}
