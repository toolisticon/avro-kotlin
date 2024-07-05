package io.toolisticon.kotlin.avro.generator.processor

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.context.AvroKotlinGeneratorContextFactory
import io.toolisticon.kotlin.avro.generator.processor.AddJavaAnnotationsByPropertyProcessor.Companion.createAnnotationSpec
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.property.JavaAnnotationProperty
import io.toolisticon.kotlin.avro.value.property.javaAnnotations
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@Target(AnnotationTarget.CLASS)
annotation class Hello(val name: String)

@Target(AnnotationTarget.CLASS)
annotation class World(val some: Int)

@Target(AnnotationTarget.FIELD)
annotation class WorldField

internal class AddJavaAnnotationsByPropertyProcessorTest {


  private val factory = AvroKotlinGeneratorContextFactory()
  private val processor = AddJavaAnnotationsByPropertyProcessor()

  private val avscWithAnnotations: AvroSchema = AvroKotlin.parseSchema(
    JsonString.of(
      """
      {
        "name": "xxx.Foo",
        "type": "record",
        "javaAnnotation": [
          "io.toolisticon.avro.avro4k.generator.processor.Hello(name=\"the name\")",
          "io.toolisticon.avro.avro4k.generator.processor.World(some=5)"
        ],
        "fields": [
          {"name":"f", "type":"string",
            "javaAnnotation":"io.toolisticon.avro.avro4k.generator.processor.WorldField()"
          },
          {"name":"h", "type":"string",
            "javaAnnotation":"io.toolisticon.avro.avro4k.generator.processor.WorldField"
          },
          {"name":"g", "type":"int"}
        ]
      }
    """.trimIndent()
    )
  )

  @Test
  fun `read 2 annotations from type`() {
    assertThat(avscWithAnnotations.properties.javaAnnotations).containsExactly(
      JavaAnnotationProperty("io.toolisticon.avro.avro4k.generator.processor.Hello(name=\"the name\")"),
      JavaAnnotationProperty("io.toolisticon.avro.avro4k.generator.processor.World(some=5)"),
    )
  }

  @Test
  fun `read 1 annotation from field f and h`() {
    assertThat(avscWithAnnotations.getField(Name("f"))!!.properties.javaAnnotations).containsExactly(
      JavaAnnotationProperty("io.toolisticon.avro.avro4k.generator.processor.WorldField()"),
    )
    assertThat(avscWithAnnotations.getField(Name("h"))!!.properties.javaAnnotations).containsExactly(
      JavaAnnotationProperty("io.toolisticon.avro.avro4k.generator.processor.WorldField"),
    )
  }

  @Test
  fun `read 0 annotation from field g`() {
    assertThat(avscWithAnnotations.getField(Name("g"))!!.properties.javaAnnotations).isEmpty()
  }

  @Test
  @Disabled("fix docs")
  fun `generate data class and add annotation to type and parameter`() {
    val declaration = AvroParser().parseSchema(avscWithAnnotations.get())

    val fileSpec = AvroKotlinGenerator().generate(declaration)


    assertThat(fileSpec.code.trim()).isEqualTo(
      """
      @file:Suppress("RedundantVisibilityModifier")

      package xxx

      import io.toolisticon.avro.avro4k.generator.processor.Hello
      import io.toolisticon.avro.avro4k.generator.processor.World
      import io.toolisticon.avro.avro4k.generator.processor.WorldField
      import kotlin.Int
      import kotlin.String
      import kotlin.Suppress
      import kotlinx.serialization.Serializable
      import org.apache.avro.specific.AvroGenerated

      /**
       * Foo
       *
       */
      @Serializable
      @AvroGenerated
      @Hello(name="the name")
      @World(some=5)
      public data class Foo(
        @WorldField
        public val f: String,
        @WorldField
        public val h: String,
        public val g: Int,
      )
    """.trimIndent()
    )

  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "io.toolisticon.avro.avro4k.generator.processor.WorldField#@io.toolisticon.avro.avro4k.generator.processor.WorldField",
      "io.toolisticon.avro.avro4k.generator.processor.WorldField()#@io.toolisticon.avro.avro4k.generator.processor.WorldField",
      "io.toolisticon.avro.avro4k.generator.processor.Hello(name=\"the name\")#@io.toolisticon.avro.avro4k.generator.processor.Hello(name=\"the name\")"
    ], delimiterString = "#"
  )
  fun `create annotation spec from string`(input: String, expected: String) {
    val spec = createAnnotationSpec(JavaAnnotationProperty(input))

    assertThat(spec.code).isEqualTo(expected)
  }
}
