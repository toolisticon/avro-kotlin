@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.generation.spi.context.AbstractKotlinCodeGenerationContext
import java.time.Instant

/**
 * Concrete implementation of [AvroDeclarationContext] for a [ProtocolDeclaration].
 */
data class ProtocolDeclarationContext(
  override val registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val declaration: ProtocolDeclaration,
  override val nowSupplier: () -> Instant = AvroKotlin.nowSupplier
) : AvroDeclarationContext<ProtocolDeclaration>, AbstractKotlinCodeGenerationContext<ProtocolDeclarationContext>(registry) {
  override val contextType = ProtocolDeclarationContext::class
}
