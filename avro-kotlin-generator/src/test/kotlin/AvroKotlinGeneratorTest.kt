package io.toolisticon.kotlin.avro.generator

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.generator.TestFixtures.NOW_SUPPLER
import io.toolisticon.kotlin.avro.generator.TestFixtures.expectedSource
import io.toolisticon.kotlin.avro.generator.TestFixtures.parseDeclaration
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.model.SchemaType
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AvroKotlinGeneratorTest {

    private val generator = AvroKotlinGenerator(nowSupplier = NOW_SUPPLER)

    @Test
    fun `generate simple data class for schema`() {
        val declaration = AvroParser().parseSchema(
            SchemaBuilder.record("a.b.c.Dee")
                .doc("This is the Dee message.")
                .fields()
                .name("x")
                .doc("this is x")
                .type(
                    AvroBuilder.primitiveSchema(
                        SchemaType.STRING,
                        object : StringLogicalType(BuiltInLogicalType.UUID.logicalTypeName) {}).get()
                )
                .noDefault()
                .endRecord()
        )


        val file = generator.generate(declaration)

        println(file.code)
    }

    @Test
    fun `simple nested data class`() {
        val declaration = parseDeclaration("schema/SingleNestedRecord.avsc")

        val file = generator.generate(declaration)
        println(file.code)
    }

    @Test
    fun `generate SchemaContainingEnum`() {
        val declaration = parseDeclaration("schema/SchemaContainingEnum.avsc")
        val file = generator.generate(declaration)

        assertThat(file.code).isEqualToIgnoringWhitespace(expectedSource(file.className))
    }
}
