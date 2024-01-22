package io.toolisticon.avro.kotlin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.toolisticon.avro.kotlin.AvroKotlin.ResourceKtx.findAvroResources
import io.toolisticon.avro.kotlin.AvroKotlin.ResourceKtx.loadJsonString
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.LogicalTypes
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream
import kotlin.streams.asStream


object TestFixtures {

  val DEFAULT_PARSER = AvroParser()

  fun parseSchema(json: JsonString): Schema = Schema.Parser().parse(json.inputStream())
  fun loadSchema(resource: String): Schema = parseSchema(loadJsonString(resource))

  fun parseProtocol(json: JsonString): Protocol = Protocol.parse(json.inputStream())
  fun loadProtocol(resource: String): Protocol = parseProtocol(loadJsonString(resource))

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
            Schema.create(SchemaType.NULL.get()),
            LogicalTypes.uuid().addToSchema(Schema.create(SchemaType.STRING.get()))
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

  val fqnBankAccountCreated = CanonicalName(Namespace("lib.test.event"), Name("BankAccountCreated"))
  val fqnFindCurrentBalance = CanonicalName(Namespace("lib.test.query"), Name("FindCurrentBalance"))

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
    .type(Schema.create(SchemaType.STRING.get()))
    .noDefault()
    .endRecord()


  /**
   * Provides all protocol and schema resources contained in `src/test/resources` as a Stream,
   * used by parameterized tests.
   */
  class AvroFilesArgumentProvider : ArgumentsProvider {
    override fun provideArguments(ctx: ExtensionContext): Stream<out Arguments> = findAvroResources()
      .flatMap { (k, v) ->
        v.map { k to it }
      }.asSequence()
      .map { Arguments.arguments(it.first, it.second) }
      .asStream()
  }
}
