package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.TestFixtures.resourceUrl
import io.toolisticon.avro.kotlin._test.CustomLogicalTypeFactory
import io.toolisticon.avro.kotlin.key.AvroFingerprint
import io.toolisticon.avro.kotlin.key.AvroHashCode
import io.toolisticon.avro.kotlin.ktx.fingerprint
import io.toolisticon.avro.kotlin.ktx.hashCode
import io.toolisticon.avro.kotlin.ktx.json
import io.toolisticon.avro.kotlin.ktx.withLogicalType
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
    val s = AvroParser().parseSchema(url.readText())
    assertThat(s.name.value).isEqualTo("SimpleTypesRecord")
  }

  @Test
  fun `parse simple schema and add avroMeta property`() {
    val json = SchemaBuilder.record("foo.Bar")
      .fields()
      .name("xxx")
      .type(Schema.create(Schema.Type.STRING).withLogicalType(LogicalTypes.uuid()))
      .noDefault()
      .endRecord().json

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
    val s1 = Schema.create(Schema.Type.STRING).withLogicalType(
      LogicalTypes.uuid()
    )
    val s2 = Schema.create(Schema.Type.STRING)

    val s3 = Schema.create(Schema.Type.STRING).withLogicalType(
      LogicalTypes.uuid()
    )

    assertThat(AvroFingerprint(s1)).isEqualTo(AvroFingerprint(s2))
    assertThat(AvroHashCode(s1)).`as` { "hashcodes do not match" }.isNotEqualTo(AvroHashCode(s2))
    assertThat(s1).isEqualTo(s3)
  }

  @Test
  fun `read protocol resource`() {
    val url = resourceUrl("protocol/DummyProtocol.avpr")
    val protocol = AvroParser().parseProtocol(url)

    println(protocol.protocol.json)
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

    println(declaration.schema.json)

    println(declaration.avroTypes)

  }

}
