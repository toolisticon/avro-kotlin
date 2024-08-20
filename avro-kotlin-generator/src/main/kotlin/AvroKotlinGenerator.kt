package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.generator.strategy.AbstractDataClassFromRecordTypeStrategy
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spi.strategy.executeAll

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
@OptIn(ExperimentalKotlinPoetApi::class)
class AvroKotlinGenerator(
  private val properties: AvroKotlinGeneratorProperties = AvroKotlinGeneratorProperties(),
  private val registry: AvroCodeGenerationSpiRegistry
) {

  fun generate(declaration: SchemaDeclaration): KotlinFileSpec {
    val context = SchemaDeclarationContext.of(declaration, registry, properties)
    val recordType = declaration.recordType
    val fileSpecBuilder = KotlinFileSpecBuilder.builder(context.rootClassName)

    val dataClasses = context.strategies(AbstractDataClassFromRecordTypeStrategy::class)
      .executeAll(context, recordType)

    //context.processors.fileSpecProcessors(context, context.rootClassName, fileSpecBuilder)

    dataClasses.forEach(fileSpecBuilder::addType)

    return fileSpecBuilder.build()
  }
}
