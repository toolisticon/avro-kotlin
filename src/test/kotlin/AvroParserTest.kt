package io.toolisticon.avro.kotlin

import _ktx.ResourceKtx.loadJsonString
import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin._test.CustomLogicalTypeFactory
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.JsonSource
import io.toolisticon.avro.kotlin.value.*
import io.toolisticon.avro.kotlin.value.AvroSpecification.PROTOCOL
import io.toolisticon.avro.kotlin.value.AvroSpecification.SCHEMA
import mu.KLogging
import org.apache.avro.LogicalTypes
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File

internal class AvroParserTest {
  companion object : KLogging() {
    // empty
  }

  @Test
  fun `load from file`(@TempDir tmp: File) {
    val json = loadJsonString("schema/SchemaContainingSimpleTypes.avsc")
    val file = tmp.resolve("SchemaContainingSimpleTypes.avsc").apply {
      writeText(json.value)
    }

    val s = AvroParser().parseSchema(file)
    assertThat(s.name.value).isEqualTo("SimpleTypesRecord")
  }

  @Test
  fun `load from url`() {
    val url = resourceUrl("schema/SchemaContainingSimpleTypes.avsc")
    val s = AvroParser().parseSchema(url)
    assertThat(s.name.value).isEqualTo("SimpleTypesRecord")
  }

  @Test
  fun `read from json`() {
    val url = resourceUrl("schema/SchemaContainingSimpleTypes.avsc")
    val json = JsonString(url.readText())
    val s = AvroParser().parseSchema(json)

    assertThat(s.name).isEqualTo(Name("SimpleTypesRecord"))
    assertThat(s.source).isInstanceOf(JsonSource::class.java)
  }

  @Test
  fun `parse schema declaration from json`() {
    val json = JsonString(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(primitiveSchema(STRING, LogicalTypes.uuid()).get())
        .noDefault()
        .endRecord()
    )


    val declaration = AvroParser()
      .registerLogicalTypeFactory(CustomLogicalTypeFactory())
      .parseSchema(json)



//    val meta = schema.recordType.schema.avroKotlinMeta
//
//
//    meta.fingerprint = schema.recordType.schema.fingerprint


//    val meta = schema.avroKotlin
//

    //schema.recordType.schema.avroKotlinMeta.store("another", "hello")

  }

  @Test
  fun `schema equal`() {
    val s1 = primitiveSchema(STRING, LogicalTypes.uuid())
    val s2 = primitiveSchema(STRING)
    val s3 = primitiveSchema(STRING, LogicalTypes.uuid())

    assertThat(s1.fingerprint).isEqualTo(s2.fingerprint)
    assertThat(s1.hashCode).`as` { "hashcodes do not match" }.isNotEqualTo(s2.hashCode)
    assertThat(s1).isEqualTo(s3)
  }


  @Test
  fun `parse simple schema with reflective field`() {
    val schema = SchemaBuilder.record("foo.Bar")
      .fields()
      .requiredString("value")
      .name("self")
      .type("foo.Bar")
      .noDefault()
      .endRecord()


    val declaration = AvroParser().parseSchema(schema)

    assertThat(declaration.canonicalName).isEqualTo(CanonicalName(Namespace("foo"), Name("Bar")))
  }

  @Test
  fun `parse protocol resource`() {
    val declaration = AvroParser()
      .registerLogicalTypeFactory(CustomLogicalTypeFactory())
      .parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

    assertThat(declaration.canonicalName).hasToString("CanonicalName(fqn='foo.dummy.DummyProtocol')")

    assertThat(declaration.avroTypes).hasSize(9)
    assertThat(declaration.protocol.recordTypes).hasSize(4)

    val msg: AvroProtocol.TwoWayMessage = declaration.protocol.messages[Name("queryDummy")]!! as AvroProtocol.TwoWayMessage


    assertThat(msg.errors.isError).isTrue()

  }

  @ParameterizedTest
  @ArgumentsSource(TestFixtures.AvroFilesArgumentProvider::class)
  fun `can parse all existing resources`(spec: AvroSpecification, file: File) {
    val ignoredFiles = setOf(
      "/org.apache.avro/schema/json.avsc",
      "/protocol/protocol.avpr",
      "/protocol/namespace.avpr",
      "/protocol/namespaces.avpr",
      "/protocol/bar.avpr",
      "/protocol/reservedwords.avpr",
      "/protocol/unicode.avpr",
      "/protocol/output-protocol.avpr",
      "/protocol/output-import.avpr",
      "/protocol/output-proto.avpr",
      "/protocol/nestedimport.avpr",
      "/protocol/input-protocol.avpr",
      "/protocol/bulk-data.avpr",
      "/protocol/import.avpr",
      "/protocol/proto.avpr",
    )
    Assumptions.assumeTrue(ignoredFiles.none {
      file.path.endsWith(it)
    })

    assertThatNoException()
      .`as` { "failed to parse $spec: file://$file" }
      .isThrownBy {
        when (spec) {
          SCHEMA -> TestFixtures.DEFAULT_PARSER.parseSchema(file)
          PROTOCOL -> TestFixtures.DEFAULT_PARSER.parseProtocol(file)
        }
      }
  }
}
