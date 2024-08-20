package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.generation.spi.context.AbstractKotlinCodeGenerationContext

/**
 * Concrete implementation of [AvroDeclarationContext] for a [ProtocolDeclaration].
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class ProtocolDeclarationContext(
  registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val declaration: ProtocolDeclaration,
) : AvroDeclarationContext<ProtocolDeclaration>, AbstractKotlinCodeGenerationContext<ProtocolDeclarationContext>(registry) {
  override val contextType = ProtocolDeclarationContext::class
}
