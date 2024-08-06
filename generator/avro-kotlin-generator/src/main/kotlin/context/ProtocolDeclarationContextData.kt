package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.api.context.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinGeneratorProcessors
import io.toolisticon.kotlin.avro.generator.api.strategy.AvroKotlinGeneratorStrategies
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry

data class ProtocolDeclarationContextData(
  override val rootClassName: ClassName,
  override val isRoot: Boolean,
  override val declaration: AvroDeclaration,
  override val properties: AvroKotlinGeneratorProperties,
  override val avroPoetTypes: AvroPoetTypes,
  override val processors: AvroKotlinGeneratorProcessors,
  override val strategies: AvroKotlinGeneratorStrategies,
  override val registry: KotlinCodeGenerationSpiRegistry<ProtocolDeclarationContext>
) : ProtocolDeclarationContext
