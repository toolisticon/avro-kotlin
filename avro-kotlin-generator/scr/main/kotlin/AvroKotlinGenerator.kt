package io.toolisticon.kotlin.avro.generator

/**
 * This generator is the central class for code generation for avro schema and protocol declarations.
 * It internally uses the features provided by [io.toolisticon.kotlin.generation.KotlinCodeGeneration].
 *
 * A generator is supposed to work as a singleton, so for a generation run of multiple declarations, you would instantiate
 * this class once.
 *
 * @param properties - configuration properties that can configure the code generation behavior
 * @param registry - holds [io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationStrategy]s and [io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationProcessor]s, usually read via spi/serviceloader.
 */
class AvroKotlinGenerator(
  private val properties: AvroKotlinGeneratorProperties,
  private val registry: AvroCodeGenerationSpiRegistry
) {
}
