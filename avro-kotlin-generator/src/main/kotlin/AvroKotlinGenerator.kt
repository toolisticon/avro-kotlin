@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spi.strategy.executeAll

/**
 * Core class of `avro-kotlin-generator`. Configure via SPI/ServiceLoader and properties,
 * takes a parsed schema or protocol declaration and generates one (or more?) file-specs that
 * can be written to file system and compiled.
 */
open class AvroKotlinGenerator(
  val registry: AvroCodeGenerationSpiRegistry,
  val properties: AvroKotlinGeneratorProperties = DefaultAvroKotlinGeneratorProperties()
) {
  companion object {
    val NAME = AvroKotlinGenerator::class.java.name
  }

  constructor(
    properties: AvroKotlinGeneratorProperties = DefaultAvroKotlinGeneratorProperties(),
    classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER
  ) : this(
    properties = properties,
    registry = AvroCodeGenerationSpiRegistry.load(classLoader)
  )

  internal fun schemaDeclarationContext(declaration: SchemaDeclaration) = SchemaDeclarationContext.of(declaration, registry, properties)

  internal fun protocolDeclarationContext(declaration: ProtocolDeclaration) = ProtocolDeclarationContext.of(declaration, registry, properties)

  fun generate(declaration: SchemaDeclaration): List<KotlinFileSpec> {
    val context = schemaDeclarationContext(declaration)

    val recordType = declaration.recordType
    val fileSpecBuilder = KotlinFileSpecBuilder.builder(context.rootClassName)

    val dataClasses = context.dataClassStrategies.executeAll(context, recordType)

    dataClasses.forEach(fileSpecBuilder::addType)
    return listOf(fileSpecBuilder.build())
  }

  fun generate(declaration: ProtocolDeclaration): List<KotlinFileSpec> = with(protocolDeclarationContext(declaration)) {
    strategies(AvroFileSpecFromProtocolDeclarationStrategy::class).executeAll(
      context = this,
      input = declaration
    )
  }
}
