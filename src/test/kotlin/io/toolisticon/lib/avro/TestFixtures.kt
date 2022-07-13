package io.toolisticon.lib.avro

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import java.nio.file.FileSystem

internal object TestFixtures {
  const val SCHEMA_ROOT = "avro"

  val OM = jacksonObjectMapper().findAndRegisterModules()

  val fqnBankAccountCreated = AvroKotlinLib.fqn("lib.test.event", "BankAccountCreated")
  val fqnFindCurrentBalance = AvroKotlinLib.fqn("lib.test.query", "FindCurrentBalance")

  val windowsFs: () -> FileSystem = {
    Jimfs.newFileSystem(
      "windows",
      Configuration.windows()
    )
  }

  /**
   * A simple schema containing only one string `value`.
   */
  val schemaFoo = simpleStringValueSchema("com.acme.test", "Foo")

  /**
   * A simple schema containing only one string `value`.
   */
  val schemaBar = simpleStringValueSchema("com.acme.dummy", "Bar")

  fun simpleStringValueSchema(namespace: Namespace, name: Name) = SchemaBuilder.record(name)
    .namespace(namespace)
    .doc("some test")
    .fields()
    .name("value")
    .type(Schema.create(Schema.Type.STRING))
    .noDefault()
    .endRecord()
}
