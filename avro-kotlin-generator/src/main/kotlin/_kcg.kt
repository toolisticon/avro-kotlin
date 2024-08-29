package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.strategy.KotlinFileSpecStrategy
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import mu.KLogging

/**
 * Things we notice are useful, but should be implemented in the `kotlin code generation` lib.
 * Mark with TODO/FIXME and provide issue number when possible.
 *
 * In an ideal world, this object is empty
 */
@OptIn(ExperimentalKotlinPoetApi::class)
internal object KotlinCodeGenerationIncubator : KLogging() {


// TODO: move to kotlin-code-generation - issue #37
  /**
   * Generate source files.
   *
   * @param INPUT the type of the input (base source of generation)
   * @param CONTEXT the context (containing registry, ...) used for generation.
   * @param STRATEGY the [KotlinFileSpecStrategy] to apply (using `executeAll()`
   * @param input the instance of the input
   * @param contextFactory fn that creates the context based on input.
   * @return list of [KotlinFileSpec]
   * @throws IllegalStateException when no strategy is found.
   */
  inline fun <INPUT : Any,
    CONTEXT : KotlinCodeGenerationContext<CONTEXT>,
    reified STRATEGY : KotlinFileSpecStrategy<CONTEXT, INPUT>> generateFn(
    input: INPUT,
    contextFactory: (INPUT) -> CONTEXT,
  ): List<KotlinFileSpec> {
    val context = contextFactory.invoke(input)
    val strategies: List<STRATEGY> = context.registry.strategies.filter(STRATEGY::class).mapNotNull {
      if (it.test(context, input)) {
        it
      } else {
        logger.info { "strategy-filter: removing ${it.name}" }
        null
      }
    }
    check(strategies.isNotEmpty()) { "No applicable strategy found/filtered for `${STRATEGY::class}`." }
    return context.registry.strategies.filter(STRATEGY::class).executeAll(context, input)
  }
}
