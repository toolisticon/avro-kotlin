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

}
