package io.toolisticon.kotlin.avro.generator

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.generation.spec.KotlinFileSpecs
import mu.KLogging

/**
 * Things we notice are useful, but should be implemented in the `kotlin code generation` lib.
 * Mark with TODO/FIXME and provide issue number when possible.
 *
 * In an ideal world, this object is empty
 */
@OptIn(ExperimentalKotlinPoetApi::class)
internal object KotlinCodeGenerationIncubator : KLogging() {

  operator fun KotlinFileSpecs.plus(other: KotlinFileSpecs): KotlinFileSpecs = other.fold(this, KotlinFileSpecs::plus)

}
