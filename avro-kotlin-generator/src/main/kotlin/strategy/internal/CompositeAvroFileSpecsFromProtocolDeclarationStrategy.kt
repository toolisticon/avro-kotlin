package io.toolisticon.kotlin.avro.generator.strategy.internal

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecsFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.generation.spec.KotlinFileSpecs
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import mu.KotlinLogging

/**
 * Executes all [AvroFileSpecFromProtocolDeclarationStrategy] from context/registry if applicable.
 *
 * This is registered in the [io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry] by default,
 * additional [AvroFileSpecsFromProtocolDeclarationStrategy]s can be registered by implementers.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
internal data object CompositeAvroFileSpecsFromProtocolDeclarationStrategy : AvroFileSpecsFromProtocolDeclarationStrategy() {
  private val logger = KotlinLogging.logger(this::class.qualifiedName!!)

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpecs {
    val singleFileStrategies = context.registry.protocolFileSpecStrategies()
    logger.info { "Composite executeAll on: ${singleFileStrategies.map { it.name }}." }

    return KotlinFileSpecs.collect(*(singleFileStrategies.executeAll(context, input).map { { it } }.toTypedArray()))
  }

  override fun test(context: ProtocolDeclarationContext, input: Any): Boolean {
    return super.test(context, input) && context.registry.protocolFileSpecStrategies().isNotEmpty()
  }
}
