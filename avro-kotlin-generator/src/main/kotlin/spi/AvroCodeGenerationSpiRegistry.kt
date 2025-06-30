package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator.Companion.CONTEXT_UPPER_BOUND
import io.toolisticon.kotlin.avro.generator.logical.LogicalTypeMap
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecListFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.defaultClassLoader
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.filter.all
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.filter.hasContextType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.spi.filter.hasNameIn
import io.toolisticon.kotlin.generation.spi.*
import io.toolisticon.kotlin.generation.spi.registry.KotlinCodeGenerationSpiList
import kotlin.reflect.KClass

/**
 * An implementation of [KotlinCodeGenerationSpiRegistry] that supports avro generation specific helpers.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class AvroCodeGenerationSpiRegistry(
  registry: KotlinCodeGenerationSpiRegistry
) : KotlinCodeGenerationSpiRegistry by registry {
  companion object {
    private val hasContextType = hasContextType(CONTEXT_UPPER_BOUND)

    fun load(classLoader: ClassLoader = defaultClassLoader(), exclusions: Set<String> = emptySet()): AvroCodeGenerationSpiRegistry {
      val spiList = KotlinCodeGeneration.spi.load(classLoader)
        .filterNot(hasNameIn(exclusions))

      return AvroCodeGenerationSpiRegistry(spiList)
    }
  }

  constructor(spiList: KotlinCodeGenerationSpiList, filter: KotlinCodeGenerationSpiPredicate = all) : this(
    spiList.filter(hasContextType).filter(filter).registry()
  )

  val contextTypeUpperBound: KClass<*> = CONTEXT_UPPER_BOUND

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
  fun <PROCESSOR : KotlinCodeGenerationProcessor<CONTEXT, INPUT, BUILDER>, CONTEXT : KotlinCodeGenerationContext<CONTEXT>, INPUT : Any, BUILDER : Any> processors(
    type: KClass<PROCESSOR>
  ): List<PROCESSOR> = processors.filter(type)

  /**
   * Get filtered strategies of given type.
   */
  fun <STRATEGY : KotlinCodeGenerationStrategy<CONTEXT, INPUT, SPEC>, CONTEXT : KotlinCodeGenerationContext<CONTEXT>, INPUT : Any, SPEC : Any> strategies(type: KClass<STRATEGY>): List<STRATEGY> =
    strategies.filter(type)
}
