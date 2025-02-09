package io.toolisticon.kotlin.avro

import _ktx.ResourceKtx.loadJsonString
import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.TestFixtures.DEFAULT_PARSER
import io.toolisticon.kotlin.avro._test.CustomLogicalTypeFactory
import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.RequestType
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isError
import io.toolisticon.kotlin.avro.model.wrapper.JsonSource
import io.toolisticon.kotlin.avro.value.*
import io.toolisticon.kotlin.avro.value.AvroSpecification.PROTOCOL
import io.toolisticon.kotlin.avro.value.AvroSpecification.SCHEMA
import org.apache.avro.LogicalTypes
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File

internal class AvroParserTest {

  @Test
  @DisabledOnOs(OS.WINDOWS)
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
    val json = JsonString.of(url.readText())
    val s = AvroParser().parseSchema(json)

    assertThat(s.name).isEqualTo(Name("SimpleTypesRecord"))
    assertThat(s.source).isInstanceOf(JsonSource::class.java)
  }

  @Test
  fun `parse schema declaration from json`() {
    val json = JsonString.of(
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



    with(declaration.avroTypes) {
      assertThat(this).hasSize(2)
    }
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
    assertThat(declaration.protocol.recordTypes).hasSize(3)
    assertThat(declaration.protocol.types.findTypes<RequestType>()).hasSize(1)

    val msg: AvroProtocol.TwoWayMessage = declaration.protocol.messages[Name("queryDummy")]!! as AvroProtocol.TwoWayMessage


    assertThat(msg.errors.isError).isTrue()

  }

  @ParameterizedTest
  @ArgumentsSource(TestFixtures.AvroFilesArgumentProvider::class)
  @DisabledOnOs(OS.WINDOWS)
  fun `can parse all existing resources`(spec: AvroSpecification, file: File) {
    setOf<String>(
      // enter file here to disable its test
    ).forEach { ignoredFile ->
      assumeFalse(file.path.endsWith(ignoredFile)) { "Ignoring avro resource: $ignoredFile." }
    }

    assertThatNoException()
      .`as` { "failed to parse $spec: file://$file" }
      .isThrownBy {
        when (spec) {
          SCHEMA -> DEFAULT_PARSER.parseSchema(file)
          PROTOCOL -> DEFAULT_PARSER.parseProtocol(file)
        }
      }
  }


}
