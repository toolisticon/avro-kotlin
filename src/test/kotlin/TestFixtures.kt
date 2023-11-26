package io.toolisticon.avro.kotlin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.toolisticon.avro.kotlin.declaration.ProtocolDeclaration
import io.toolisticon.avro.kotlin.ktx.loadResource
import io.toolisticon.avro.kotlin.ktx.trailingSlash
import io.toolisticon.avro.kotlin.model.AvroSchema
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.Schema.Type
import org.apache.avro.SchemaBuilder
import java.net.URL


object TestFixtures {
  fun resourceUrl(resource: String): URL = requireNotNull(
    TestFixtures::class.java.getResource(resource.trailingSlash())
  ) { "resource not found: $resource" }


  fun loadSchemaJson(resource: String): Schema = Schema.Parser().parse(JsonString(loadResource(resource).trim()).value)

  fun loadProtocolDeclaration(resource: String): ProtocolDeclaration {
    return AvroParser().parseProtocol(JsonString(loadResource(resource)))
  }

  /**
   * this schema contains 5 types:
   *
   * * the root type Foo
   * * the map
   * * union of null and string
   * * null
   * * the string
   */
  val RECORD_MAP_WITH_NULLABLE_UUIDS = AvroSchema(
    schema = SchemaBuilder.record("xxx.yyy.Foo")
      .doc("This is the foo.")
      .fields()
      .name("aMap")
      .doc("this is a map with a nullable value")
      .type(
        Schema.createMap(
          Schema.createUnion(
            Schema.create(Type.NULL),
            LogicalTypes.uuid().addToSchema(Schema.create(Type.STRING))
          )
        )
      )
      .noDefault()
      .endRecord(), isRoot = true
  )

  /**
   * This is the avro single object encoded hex representation of `FooString(str="bar")`.
   *
   * C3 01 - avro marker bytes
   * CD 1D 19 01 C3 39 C6 61 - encoded writer schema fingerprint for lookup (4162171688006255043L).
   * 06 62 61 72 - payload: 06: 3-letters in HEX, b=62,a=61,r=72
   *
   */
  const val SINGLE_STRING_ENCODED = "[C3 01 CD 1D 19 01 C3 39 C6 61 06 62 61 72]"

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
