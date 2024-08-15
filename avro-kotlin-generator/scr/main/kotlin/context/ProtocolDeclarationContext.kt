package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry

/**
 * Concrete implementation of [AvroDeclarationContext] for a [ProtocolDeclaration].
 */
class ProtocolDeclarationContext(
  override val registry: KotlinCodeGenerationSpiRegistry<AvroDeclarationContext<*>>,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val declaration: ProtocolDeclaration,
) : AvroDeclarationContext<ProtocolDeclaration>
