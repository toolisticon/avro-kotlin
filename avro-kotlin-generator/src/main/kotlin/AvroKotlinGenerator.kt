@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import java.time.Instant


class AvroKotlinGenerator(
  private val registry: AvroCodeGenerationSpiRegistry,
  private val properties: AvroKotlinGeneratorProperties = AvroKotlinGeneratorProperties(),
  private val nowSupplier: () -> Instant = AvroKotlin.nowSupplier
) {
  companion object {
    val NAME = AvroKotlinGenerator::class.java.name
  }

  constructor(
    properties: AvroKotlinGeneratorProperties = AvroKotlinGeneratorProperties(),
    classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER,
    nowSupplier: () -> Instant = AvroKotlin.nowSupplier
  ) : this(
    properties = properties,
    registry = AvroCodeGenerationSpiRegistry.load(classLoader),
    nowSupplier = nowSupplier
  )

  fun generate(declaration: SchemaDeclaration): KotlinFileSpec {
    val context = SchemaDeclarationContext.of(declaration, registry, properties)
      .copy(nowSupplier = this@AvroKotlinGenerator.nowSupplier)

    val recordType = declaration.recordType
    val fileSpecBuilder = KotlinFileSpecBuilder.builder(context.rootClassName)

    val dataClasses = context.dataClassStrategies.executeAll(context, recordType)

    //context.processors.fileSpecProcessors(context, context.rootClassName, fileSpecBuilder)

    dataClasses.forEach(fileSpecBuilder::addType)

    return fileSpecBuilder.build()
  }
}
