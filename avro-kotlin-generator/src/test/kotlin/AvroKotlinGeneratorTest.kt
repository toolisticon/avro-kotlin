package io.toolisticon.kotlin.avro.generator

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.generator.strategy.DefaultDataClassRecordStrategy
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry
import io.toolisticon.kotlin.generation.spi.registry.KotlinCodeGenerationServiceRepository
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.junit.jupiter.api.Test

@OptIn(com.squareup.kotlinpoet.ExperimentalKotlinPoetApi::class)
internal class AvroKotlinGeneratorTest {

    @Test
    fun `generate simple data class for schema`() {
        val declaration = AvroParser().parseSchema(
            SchemaBuilder.record("a.b.c.Dee")
                .fields()
                .requiredString("x")
                .endRecord()
        )

        val registry = AvroCodeGenerationSpiRegistry(registry = KotlinCodeGenerationServiceRepository(contextType = Any::class, spiInstances = listOf(
            DefaultDataClassRecordStrategy()
        )))

        val generator = AvroKotlinGenerator(registry = registry)

        val file = generator.generate(declaration)

        println(file.code)
    }
}
