package io.toolisticon.kotlin.avro.generator.context

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Avro4kGeneratorContextFactoryTest {


  @Test
  fun `plain single string record`() {
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .requiredString("foo")
        .endRecord()
    )

    val factory = AvroKotlinGeneratorContextFactory(properties = AvroKotlinGeneratorProperties(schemaTypeSuffix = "Data"))

    val ctx = factory.create(declaration)

    println(ctx)

    assertThat(ctx.avroPoetTypes).hasSize(1)

  }
}
