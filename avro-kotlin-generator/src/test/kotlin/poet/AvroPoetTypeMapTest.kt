package io.toolisticon.kotlin.avro.generator.poet

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.rootClassName
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.Schema.Type
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("configure spi")
@OptIn(com.squareup.kotlinpoet.ExperimentalKotlinPoetApi::class)
internal class AvroPoetTypeMapTest {

  private val registry = AvroCodeGenerationSpiRegistry(KotlinCodeGeneration.spi.repository(Any::class))
  private val logicalTypes = registry.logicalTypes

  private fun avroPoetTypes(declaration: SchemaDeclaration) = AvroPoetTypeMap.avroPoetTypeMap(
    rootClassName = rootClassName(declaration),
    properties = AvroKotlinGeneratorProperties(),
    avroTypes = declaration.avroTypes,
    logicalTypeMap = logicalTypes,
  )

  @Test
  fun `create map with simple string`() {
    val declaration = AvroParser().parseSchema(
      resourceUrl("schema/SimpleStringRecord.avsc")
    )

    val poetTypes = avroPoetTypes(declaration)

    assertThat(poetTypes).hasSize(2)
    assertThat(poetTypes.first().typeName.toString()).isEqualTo("kotlin.String")
  }

  @Test
  fun `resolve uuid logical type`() {
    val uuid = LogicalTypes.uuid().addToSchema(Schema.create(Type.STRING))
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(uuid)
        .noDefault()
        .endRecord()
    )
    val poetTypes = avroPoetTypes(declaration)
    assertThat(poetTypes).hasSize(2)
    assertThat(poetTypes.first().typeName.toString()).isEqualTo("java.util.UUID")
  }

  @Test
  fun `resolve uuid logical type union`() {

    val uuid = LogicalTypes.uuid().addToSchema(Schema.create(Type.STRING))
    val union = Schema.createUnion(Schema.create(Type.NULL), uuid)
    val unionHashcode = AvroHashCode.of(union)
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(union)
        .noDefault()
        .endRecord()
    )


    val poetTypes = avroPoetTypes(declaration)

    assertThat(poetTypes).hasSize(3)
    assertThat(poetTypes[unionHashcode].typeName.toString()).isEqualTo("java.util.UUID?")
  }

  @Test
  fun `resolve array of bigDecimals`() {
    val decimal: Schema = LogicalTypes.decimal(6, 2)
      .addToSchema(Schema.create(Type.BYTES))

    val array: Schema = Schema.createArray(decimal)
    val arrayKey = AvroHashCode.of(array)

    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(array)
        .noDefault()
        .endRecord()
    )

    // 3 types: foo.Bar, the array, bytes
    assertThat(declaration.avroTypes).hasSize(3)

    val poetTypes = avroPoetTypes(declaration)

    assertThat(poetTypes).hasSize(3)
    assertThat(poetTypes[arrayKey].typeName.toString())
      .isEqualTo("kotlin.collections.List<java.math.BigDecimal>")
  }

  @Test
  fun `resolve map of instant`() {
    val instant: Schema = LogicalTypes.timestampMicros()
      .addToSchema(Schema.create(Type.LONG))

    val map: Schema = Schema.createMap(instant)

    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(map)
        .noDefault()
        .endRecord()
    )
    val poetTypes = avroPoetTypes(declaration)

    assertThat(poetTypes).hasSize(3)
    assertThat(poetTypes[AvroHashCode.of(map)].typeName.toString())
      .isEqualTo("kotlin.collections.Map<kotlin.String, java.time.Instant>")
  }

  @Test
  fun `resolve nested enum `() {
    val foos: Schema = Schema.createEnum("TestEnum", "just a dummy", "foo", listOf("FOO", "BAR"), "FOO")

    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("xxx")
        .type(foos)
        .noDefault()
        .endRecord()
    )
    val poetTypes = avroPoetTypes(declaration)

    assertThat(poetTypes).hasSize(2)
    assertThat(poetTypes[AvroHashCode.of(foos)].typeName.toString())
      .isEqualTo("foo.Bar.TestEnum")
  }


  @Test
  fun `resolve nested record`() {
    val foo = SchemaBuilder.record("Foo")
      .fields()
      .requiredString("y")
      .endRecord()

    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("io.xxx.Bar")
        .fields()
        .name("xxx")
        .type(foo)
        .noDefault()
        .endRecord()
    )
    val poetTypes = avroPoetTypes(declaration)

    assertThat(poetTypes[AvroHashCode.of(foo)].typeName.toString())
      .isEqualTo("io.xxx.Bar.Foo")
  }
}
