package io.toolisticon.kotlin.avro.generator

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.serialization.serializer.IntLogicalTypeSerializer
import org.apache.avro.LogicalTypes
import org.apache.avro.SchemaBuilder
import org.junit.jupiter.api.Test

@OptIn(com.squareup.kotlinpoet.ExperimentalKotlinPoetApi::class)
internal class AvroKotlinGeneratorTest {

  @Test
  fun `generate simple data class for schema`() {
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("a.b.c.Dee")
        .doc("This is the Dee message.")
        .fields()
        .name("x")
        .doc("this is x")
        .type(AvroBuilder.primitiveSchema(SchemaType.STRING, object: StringLogicalType(BuiltInLogicalType.UUID.logicalTypeName){}).get())
        .noDefault()
        .endRecord()
    )

    val registry = AvroCodeGenerationSpiRegistry(registry = AvroCodeGenerationSpiRegistry.load())

    val generator = AvroKotlinGenerator(registry = registry)

    val file = generator.generate(declaration)

    println(file.code)
  }
}
