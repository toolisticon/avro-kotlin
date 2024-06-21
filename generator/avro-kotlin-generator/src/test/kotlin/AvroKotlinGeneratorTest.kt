package io.toolisticon.kotlin.avro.generator

import _ktx.ResourceKtx.resourceUrl
import com.github.avrokotlin.avro4k.AvroName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.spi.MavenProperties
import io.toolisticon.kotlin.generation.test.KotlinCodeGenerationTest
import io.toolisticon.kotlin.generation.test.model.KotlinCompilationCommand
import mu.KLogging
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.Schema.Type
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Instant

internal class AvroKotlinGeneratorTest {
  companion object : KLogging()

  val now = Instant.now()

  init {
    MavenProperties.nowSupplier = { now }
  }

  val properties = AvroKotlinGeneratorProperties()

  @Test
  fun `with nested enum`() {

    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("a.b.c.Dee")
        .fields()
        .name("x")
        .type(
          Schema.createEnum("SomeEnum", "an enum", "a.b.c", listOf("HA", "HI", "HO"))
        )
        .noDefault()
        .endRecord()
    )

    val generator = AvroKotlinGenerator(AvroKotlinGeneratorProperties(schemaTypeSuffix = "Data"))

    val file = generator.generate(declaration)

    val someEnumData = file.get().members.filterIsInstance<TypeSpec>()
      .find { "DeeData" == it.name }
      ?.typeSpecs?.find { "SomeEnumData" == it.name }
    assertThat(someEnumData).isNotNull

    val avroName = someEnumData!!.annotations.find { AvroName::class.asTypeName() == it.typeName }

    assertThat(avroName?.members?.first()).isEqualTo(CodeBlock.of("%S", "SomeEnum"))

  }

  @Test
  fun `create with map type`() {


    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("xxx.yyy.Foo")
        .doc("This is the foo.")
        .fields()
        .name("aMap")
        .type(
          Schema.createMap(

            Schema.createUnion(
              Schema.create(Type.NULL),
              LogicalTypes.uuid().addToSchema(Schema.create(Type.STRING))
            )
          )
        )
        .noDefault()
        .endRecord()
    )
    val file = AvroKotlinGenerator(properties).generate(declaration)

    println(file)
  }

  @Test
  @Disabled("fix docs")
  fun `generate SimpleStringRecord`() {
    val json = resourceUrl("schema/SimpleStringRecord.avsc")
    val declaration = AvroParser().parseSchema(json)

    val file = AvroKotlinGenerator(properties).generate(declaration)
    logger.info { file.toString() }

    val result = KotlinCodeGenerationTest.compile(
      KotlinCompilationCommand(file)
    )

    result.shouldBeOk()

    assertThat(file.code)
      .isEqualTo(
        """
        @file:Suppress("RedundantVisibilityModifier")

        package io.acme.schema

        import kotlin.String
        import kotlin.Suppress
        import kotlinx.serialization.Serializable
        import org.apache.avro.specific.AvroGenerated

        /**
         * This is a record with a simple string value.
         *
         * @param stringValue - The single string value, no extras.
         */
        @Serializable
        @AvroGenerated
        public data class SimpleStringRecord(
          public val stringValue: String,
        )

      """.trimIndent()
      )
  }

  @Test
  fun `generate for protocol`() {
    val declaration = AvroParser().parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

    val file = AvroKotlinGenerator(properties).generate(declaration)

    println(file)
  }
}
