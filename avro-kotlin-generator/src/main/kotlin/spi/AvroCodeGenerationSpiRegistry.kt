package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.logical.LogicalTypeMap
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecListFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.defaultClassLoader
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationProcessor
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationStrategy
import io.toolisticon.kotlin.generation.spi.processor.KotlinCodeGenerationProcessorList
import io.toolisticon.kotlin.generation.spi.registry.KotlinCodeGenerationServiceRepository
import io.toolisticon.kotlin.generation.spi.strategy.KotlinCodeGenerationStrategyList
import kotlin.reflect.KClass

/**
 * An implementation of [KotlinCodeGenerationSpiRegistry] that supports avro generation specific helpers.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class AvroCodeGenerationSpiRegistry(registry: KotlinCodeGenerationSpiRegistry) : KotlinCodeGenerationSpiRegistry by registry {
  companion object {
    private val CONTEXT_UPPER_BOUND = AvroDeclarationContext::class

    fun load(classLoader: ClassLoader = defaultClassLoader(), exclusions: Set<String> = emptySet()): AvroCodeGenerationSpiRegistry {
      val registry = KotlinCodeGeneration.spi.registry(
        contextTypeUpperBound = AvroDeclarationContext::class,
        classLoader = classLoader,
        exclusions = exclusions
      )
      return AvroCodeGenerationSpiRegistry(registry)
    }
  }

  constructor(strategies: KotlinCodeGenerationStrategyList, processors: KotlinCodeGenerationProcessorList = KotlinCodeGenerationProcessorList()) : this(
    registry = KotlinCodeGenerationServiceRepository(CONTEXT_UPPER_BOUND, processors, strategies)
  )

  override val contextTypeUpperBound: KClass<*> = CONTEXT_UPPER_BOUND

  /**
   * Map of all registered logical types.
   */
  val logicalTypes: LogicalTypeMap = LogicalTypeMap(this)


  /**
   * Filter all strategies that create a fileSpecList for a given protocol declaration.
   */
  fun protocolFileSpecListStrategies() = strategies(AvroFileSpecListFromProtocolDeclarationStrategy::class)

  /**
   * Filter all strategies that create a fileSpec for a given protocol declaration.
   */
  fun protocolFileSpecStrategies() = strategies(AvroFileSpecFromProtocolDeclarationStrategy::class)

  /**
   * Get filtered processors of given type.
   */
  fun <PROCESSOR : KotlinCodeGenerationProcessor<CONTEXT, INPUT, BUILDER>, CONTEXT : KotlinCodeGenerationContext<CONTEXT>, INPUT : Any, BUILDER : Any> processors(type: KClass<PROCESSOR>): List<PROCESSOR> = processors.filter(type)

  /**
   * Get filtered strategies of given type.
   */
  fun <STRATEGY : KotlinCodeGenerationStrategy<CONTEXT, INPUT, SPEC>, CONTEXT : KotlinCodeGenerationContext<CONTEXT>, INPUT : Any, SPEC : Any> strategies(type: KClass<STRATEGY>): List<STRATEGY> = strategies.filter(type)
}
