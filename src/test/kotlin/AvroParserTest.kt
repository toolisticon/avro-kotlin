package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.TestFixtures.resourceUrl
import io.toolisticon.avro.kotlin._test.CustomLogicalTypeFactory
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.JsonSource
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import mu.KLogging
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AvroParserTest {
  companion object : KLogging() {
    // empty
  }

  @Test
  fun `load from file`(@TempDir tmp: File) {
    val json = resourceUrl("schema/SchemaContainingSimpleTypes.avsc").readText()
    val file = tmp.resolve("SchemaContainingSimpleTypes.avsc").apply {
      writeText(json)
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
  fun `parse simple schema and add avroMeta property`() {
    val json = JsonString(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(primitiveSchema(Schema.Type.STRING, LogicalTypes.uuid()).schema)
        .noDefault()
        .endRecord()
    )

    val declaration = AvroParser()
      .registerLogicalTypeFactory(CustomLogicalTypeFactory())
      .parseSchema(json)


    println(declaration)

//    val meta = schema.recordType.schema.avroKotlinMeta
//
//    println(schema.recordType.schema.json)
//    println(schema.hashCode())
//
//    meta.fingerprint = schema.recordType.schema.fingerprint


//    val meta = schema.avroKotlin
//
//    //println(meta.fingerprint)
//    println(meta)

    //println(LogicalTypes.getCustomRegisteredTypes())

    //schema.recordType.schema.avroKotlinMeta.store("another", "hello")

  }

  @Test
  fun `schema equal`() {
    val s1 = primitiveSchema(Schema.Type.STRING, LogicalTypes.uuid())
    val s2 = primitiveSchema(Schema.Type.STRING)
    val s3 = primitiveSchema(Schema.Type.STRING, LogicalTypes.uuid())

    assertThat(s1.fingerprint).isEqualTo(s2.fingerprint)
    assertThat(s1.hashCode).`as` { "hashcodes do not match" }.isNotEqualTo(s2.hashCode)
    assertThat(s1).isEqualTo(s3)
  }

  @Test
  fun `spike - reference or value on reused schema`() {
    val schema = AvroParser().parseSchema(resourceUrl("schema/ReUsingTypes.avsc"))

    //schema.recordType.schema.getField("reusing").schema().avroKotlinMeta.store("foo", "bar")

    println(schema.recordType.schema.json)
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

    assertThat(declaration.canonicalName).hasToString("foo.dummy.DummyProtocol")

    assertThat(declaration.avroTypes).hasSize(10)

  }
}
